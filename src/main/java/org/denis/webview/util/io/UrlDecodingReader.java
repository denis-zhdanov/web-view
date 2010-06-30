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
public class UrlDecodingReader extends AbstractReplacingFilterReader {

    private final CharsetDecoder decoder = Charset.forName("ISO-8859-1").newDecoder();

    /** Buffer for holding raw symbol bytes during conversion to symbol. */
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(1);

    /** Buffer to store symbol converted from raw bytes. */
    private final CharBuffer charBuffer = CharBuffer.allocate(1);

    public UrlDecodingReader(Reader in) {
        super(in, 3/* '3' is a length of percent symbol followed by two hex symbols */);
    }

    @Override
    protected int copy(DataContext dataContext) throws IllegalStateException {
        int result = 0;
        for (
            int externalBufferOffset = dataContext.externalOffset,
                externalBufferMaxOffset = dataContext.externalOffset + dataContext.externalLength;
            dataContext.internalStart < dataContext.internalEnd && externalBufferOffset < externalBufferMaxOffset;
            ++dataContext.internalStart, ++result)
        {
            switch (dataContext.internalBuffer[dataContext.internalStart]) {
                case '+':
                    dataContext.externalBuffer[externalBufferOffset++] = ' ';
                    break;
                case '%':
                    if (dataContext.internalEnd - dataContext.internalStart < 3) {
                        // Internal buffer doesn't contain enough data to decode the symbol.
                        return  result;
                    }
                    dataContext.externalBuffer[externalBufferOffset++]
                        = decode(dataContext.internalBuffer, dataContext.internalStart + 1);
                    dataContext.internalStart += 2;
                    break;
                default:
                    dataContext.externalBuffer[externalBufferOffset++]
                        = dataContext.internalBuffer[dataContext.internalStart];
            }
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
     * @throws IllegalStateException     if buffer data at the given offset is inconsistent with url encoding rules
     */
    private char decode(char[] buf, int offset) throws IllegalStateException {
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
     * @throws IllegalStateException     if given symbols don't represent valid hex value
     */
    private static byte toByte(char[] buffer, int offset) throws IllegalStateException {
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
}