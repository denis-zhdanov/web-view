package org.denis.webview.util.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Symbol stream that wraps target {@link CharBuffer}.
 * <p/>
 * Not thread-safe.
 *
 * @author Denis Zhdanov
 * @since 6/4/11
 */
public class CharBufferReader extends Reader {

    private final Listener listener;
    private CharBuffer buffer;

    public CharBufferReader(CharBuffer buffer, Listener listener) {
        this.buffer = buffer;
        this.listener = listener;
    }

    @Override
    public int read() throws IOException {
        if (!buffer.hasRemaining()) {
            listener.onBufferEmpty();
        }

        if (buffer.hasRemaining()) {
            return buffer.get();
        }
        return -1;
    }

    @Override
    public int read(char[] buf, int off, int len) throws IOException {
        if (!buffer.hasRemaining()) {
            listener.onBufferEmpty();
        }

        if (buffer.hasRemaining()) {
            int charsToCopy = Math.min(len, buffer.remaining());
            buffer.get(buf, off, charsToCopy);
            return charsToCopy;
        }

        return -1;
    }


    @Override
    public void close() throws IOException {
        listener.close();
    }

    public CharBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(CharBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Defines a contract for callback to be notified on current stream state change.
     */
    public interface Listener {

        /**
         * Called when {@link CharBufferReader#close()} is called on the target reader.
         *
         * @throws IOException      as declared by {@link Reader#close()}
         */
        void close() throws IOException;

        /**
         * Notifies that {@link CharBufferReader#getBuffer() wrapped buffer} is detected to be empty
         * on client request for the data.
         * <p/>
         * It's expected that the client pushes new data to the currently wrapped buffer or
         * {@link CharBufferReader#setBuffer(CharBuffer) provides new buffer}.
         */
        void onBufferEmpty();
    }
}
