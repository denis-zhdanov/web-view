package org.denis.webview.util.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Reader that stops reading iteration every time it detects that underlying symbol stream contains '&' characters
 * and notifies target callback about that.
 * <p/>
 * Not thread-safe.
 *
 * @author Denis Zhdanov
 * @since 6/2/11
 */
public class HttpParametersReader extends FilterReader {

    private static final char SEPARATOR = '&';

    private final Runnable listener;
    private CharBuffer buffer = CharBuffer.allocate(16);

    public HttpParametersReader(Reader in, Runnable listener) {
        super(in);
        this.listener = listener;
        buffer.limit(0);
    }

    @Override
    public int read() throws IOException {
        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (c != SEPARATOR) {
                if (!buffer.hasRemaining()) {
                    buffer.clear();
                }
                return c;
            }
        }
        int result = super.read();
        while (result == SEPARATOR) {
            listener.run();
            result = super.read();
        }
        return result;
    }

    @Override
    public int read(char[] buf, int off, int len) throws IOException {
        // Read from buffer.
        if (buffer.hasRemaining()) {
            char[] data = buffer.array();
            for (int i = buffer.position(), max = buffer.limit(); i < max; ++i) {
                if (data[i] == SEPARATOR) {
                    listener.run();
                    int read = i - buffer.position();
                    copyFromBuffer(buf, off, Math.min(len, read));
                    return read;
                }
            }
            int read = Math.min(len, buffer.remaining());
            copyFromBuffer(buf, off, read);
            return read;
        }

        // Read from underlying stream.
        int read = super.read(buf, off, len);
        for (int i = 0; i < read; ++i) {
            if (buf[off + i] != SEPARATOR) {
                continue;
            }

            listener.run();
            int symbolsToStore = read - i - 1;
            if (symbolsToStore <= 0) {
                return i;
            }

            if (symbolsToStore > buffer.capacity()) {
                buffer = CharBuffer.allocate(symbolsToStore);
            }
            buffer.clear();
            buffer.put(buf, off + i + 1, symbolsToStore);
            buffer.flip();
            return i;
        }
        return read;
    }

    private void copyFromBuffer(char[] buf, int off, int len) {
        System.arraycopy(buffer.array(), buffer.position(), buf, off, len);
        if (buffer.position() + len < buffer.limit() - 1) {
            buffer.position(buffer.position() + len + 1);
        } else {
            buffer.clear();
            buffer.limit(0);
        }
    }
}
