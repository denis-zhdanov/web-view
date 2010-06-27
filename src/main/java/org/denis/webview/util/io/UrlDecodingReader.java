package org.denis.webview.util.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Decorates {@link Reader symbols stream in order to provide transparent decoding of
 * <a href="http://www.w3schools.com/TAGS/ref_urlencode.asp">url encoded</a> text.
 * <p/>
 * Not thread-safe.
 *
 * @author Denis Zhdanov
 * @since Jun 26, 2010
 */
public class UrlDecodingReader extends FilterReader {

    private final CharsetDecoder decoder = Charset.forName("ISO-8859-1").newDecoder();

    /** Buffer for holding raw symbol bytes during conversion to symbol. */
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(1);

    /** Buffer to store symbol converted from raw bytes. */
    private final CharBuffer charBuffer = CharBuffer.allocate(1);

    /**
     * Internal buffer large enough to perform single symbol decoding if necessary.
     * <p/>
     * Is assumed to contain non-decoded data.
     */
    private char[] internalBuffer = new char[3];

    /**
     * Index within {@link #internalBuffer} that points to position of the first stored symbol.
     */
    private int internalBufferStartOffset;

    /**
     * Index within {@link #internalBuffer} that points to position just after the last stored symbol.
     */
    private int internalBufferEndOffset;

    public UrlDecodingReader(Reader in) {
        super(in);
    }

    /**
     * Reads characters from the wrapped stream into a portion of a given array performing
     * <a href="http://www.w3schools.com/TAGS/ref_urlencode.asp">url decoding</a> if necessary.
     *
     * @param buf       destination buffer
     * @param off       offset at which to start storing characters
     * @param len       maximum number of characters to read
     * @return          the number of characters read, or <code>-1</code> if the end of the stream has been reached
     * @throws IllegalArgumentException     if any of the given arguments is invalid or if given buffer
     *                                      contains data that is inconsistent with url encoding rules
     * @throws IOException                  in case of unexpected exception during I/O processing
     */
    @Override
    public int read(char[] buf, int off, int len) throws IllegalArgumentException, IOException {
        checkReadArguments(buf, off, len);

        // The general idea is to do the following:
        //   1. Check if internal buffer contains data from previous read iterations. Put it to the
        //      given buffer if any;
        //   2. Check if given buffer still has free space. Return if false;
        //   3. Read raw characters to internal buffer;
        //   4. Iterate that internal buffer performing decoding if necessary and copying as much data
        //      as possible to the given buffer;

        int copiedCharactersNumber = copyFromInternalBuffer(buf, off, len);
        if (copiedCharactersNumber == len) {
            return len;
        }

        int lengthToUse = len - copiedCharactersNumber;
        initInternalBuffer(len);
        int read = super.read(internalBuffer, internalBufferStartOffset, lengthToUse - getCachedSymbolsNumber());
        if (read < 0) {
            if (getCachedSymbolsNumber() > 0) {
                throw new IllegalArgumentException(String.format("Detected situation that target symbol stream ends "
                        + "with the data that is inconsistent with url encoding rules - '%s'",
                        new String(internalBuffer, internalBufferStartOffset, getCachedSymbolsNumber())));
            }
            return read;
        }

        internalBufferEndOffset += read;
        return copyFromInternalBuffer(buf, off, lengthToUse);
    }

    /**
     * Checks if given arguments are valid in term of {@link #read(char[], int, int)} contract.
     *
     * @param buf       char buffer to use
     * @param off       offset to use within the given buffer
     * @param len       available length to use with the given buffer
     * @throws IllegalArgumentException     if any of the given parameters is invalid
     */
    private void checkReadArguments(char[] buf, int off, int len) throws IllegalArgumentException {
        if (buf == null) {
            throw new IllegalArgumentException(String.format("Can't read data to the given char buffer. "
                    + "Reason: it is null. Offset: %d, length: %d", off, len));
        }

        if (off < 0 || off >= buf.length) {
            throw new IllegalArgumentException(String.format("Can't read data to the given char buffer (size %d). "
                    + "Reason: given offset is invalid (%d). Length: %d", buf.length, off, len));
        }

        if (len <= 0 || off + len > buf.length) {
            throw new IllegalArgumentException(String.format("Can't read data to the given char buffer "
                    + "(size %d, offset %d). Reason: given length is invalid (%d). It's expected to belong "
                    + "to [1; %d] range", buf.length, off, len, buf.length - off));
        }
    }

    /**
     * Copies as much data as possible from internal buffer to the given one performing url decoding if necessary.
     *
     * @param buf       target buffer to hold decoded data
     * @param off       offset to use within the given buffer
     * @param len       max length to use within the given buffer
     * @return          number of characters copied to the given buffer (is guaranteed to be not more
     *                  than the given length)
     * @throws IllegalArgumentException     if internal buffer contains data that is inconsistent
     *                                      with url encoding rules
     */
    private int copyFromInternalBuffer(char[] buf, int off, int len) throws IllegalArgumentException {
        if (internalBufferEndOffset <= internalBufferStartOffset) {
            // Nothing to copy.
            return 0;
        }

        int result = 0;
        for (
                int externalBufferOffset = off, externalBufferMaxOffset = off + len;
                internalBufferStartOffset < internalBufferEndOffset && externalBufferOffset < externalBufferMaxOffset;
                ++internalBufferStartOffset, ++result)
        {
            switch (internalBuffer[internalBufferStartOffset]) {
                case '+':
                    buf[externalBufferOffset++] = ' ';
                    break;
                case '%':
                    if (internalBufferEndOffset - internalBufferStartOffset < 3) {
                        // Internal buffer doesn't contain enough data to decode the symbol.
                        return  result;
                    }
                    buf[externalBufferOffset++] = decode(internalBuffer, internalBufferStartOffset + 1);
                    internalBufferStartOffset += 2;
                    break;
                default:
                    buf[externalBufferOffset++] = internalBuffer[internalBufferStartOffset];
            }
        }

        if (internalBufferStartOffset == internalBufferEndOffset) {
            internalBufferStartOffset = internalBufferEndOffset = 0;
        }
        return result;
    }

    /**
     * Decodes character assuming that it's contained at the given buffer and given offset points
     * to position of the first of two hexadecimal digits corresponding to the character values
     * in the <code>ISO-8859-1</code> character-set.
     *
     * @param buf       target buffer
     * @param offset    offset within the given buffer that points to the first of the two hexadecimal digits
     *                  corresponding to the character values in the <code>ISO-8859-1</code> character-set
     * @return          decoded character
     * @throws IllegalArgumentException     if buffer data at the given offset is inconsistent with url encoding rules
     */
    private char decode(char[] buf, int offset) throws IllegalArgumentException {
        byteBuffer.clear();
        byteBuffer.put(toByte(buf, offset));
        byteBuffer.flip();
        charBuffer.clear();
        decoder.decode(byteBuffer, charBuffer, true);
        charBuffer.flip();
        return charBuffer.get();
    }

    /**
     * Decodes two symbols from the given array at the given offset to numeric value assuming that the symbols
     * define hex string representation of target value, i.e. both <code>'buffer[offset]'</code> and
     * <code>'buffer[offset + 1]'</code> symbols belong to one of the following intervals:
     * <code>[0; 9]</code>, <code>[A; F]</code> or <code>[a; f]</code>
     *
     * @param buffer     target symbols buffer which data portion is to be decoded to its numeric representation
     * @param offset     offset to use within the given buffer
     * @return           byte corresponding to the hex symbols from the given array at the givne offset
     * @throws IllegalArgumentException     if given symbols don't represent valid hex value
     */
    private static byte toByte(char[] buffer, int offset) throws IllegalArgumentException {
        int result = 0;
        for (int i = offset; i < offset + 2; ++i) {
            char c = buffer[i];
            result <<= 4;
            if (c >= '0' && c <= '9') {
                result |= c - '0';
                continue;
            }
            if (c >= 'A' && c <= 'F') {
                result |= c - 'A' + 10;
                continue;
            }
            if (c >= 'a' && c <= 'f') {
                result |= c - 'a' + 10;
                continue;
            }
            throw new IllegalArgumentException(String.format("Can't convert given symbol (%c) to byte. Reason: the "
                + "symbol is expected to be valid hex symbol, i.e. belong to one of the following intervals: "
                + "[0; 9], [A; F], [a; f]", c));
        }
        return (byte) result;
    }

    private int getCachedSymbolsNumber() {
        return internalBufferEndOffset - internalBufferStartOffset;
    }

    /**
     * Expands internal buffer if necessary in accordance with the given client buffer length.
     *
     * @param clientBufferLength        length of the client buffer used during reading
     */
    private void initInternalBuffer(int clientBufferLength) {
        // Keep internal buffer as is if it's trailing free space is big enough.
        if (clientBufferLength - getCachedSymbolsNumber() <= internalBuffer.length - internalBufferEndOffset) {
            return;
        }

        // Perform internal buffer defragmentation if that frees continuous empty space that is big enough to
        // hold target data length.
        if (clientBufferLength <= internalBuffer.length) {
            System.arraycopy(internalBuffer, internalBufferStartOffset, internalBuffer, 0, getCachedSymbolsNumber());
            internalBufferEndOffset = getCachedSymbolsNumber();
            internalBufferStartOffset = 0;
            return;
        }

        // Create new buffer that is wide enough and replace currently used one with it.
        char[] newBuffer = new char[clientBufferLength];
        if (getCachedSymbolsNumber() > 0) {
            System.arraycopy(internalBuffer, internalBufferStartOffset, newBuffer, 0, getCachedSymbolsNumber());
        }
        internalBuffer = newBuffer;
        internalBufferEndOffset = getCachedSymbolsNumber();
        internalBufferStartOffset = 0;
    }
}
