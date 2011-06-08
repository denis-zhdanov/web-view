package org.denis.webview.util.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Wraps target symbol stream and allows to check how many symbols have been read so far.
 * <p/>
 * Not thread-safe.
 * 
 * @author Denis Zhdanov
 * @since 6/7/11 6:16 PM
 */
public class SymbolCountingReader extends FilterReader {

    private int readSymbolsNumber;
    
    public SymbolCountingReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        final int result = super.read();
        if (result > 0) {
            ++readSymbolsNumber;
        }
        return result;
    }

    @Override
    public int read(char[] buf, int off, int len) throws IOException {
        final int result = super.read(buf, off, len);
        if (result > 0) {
            readSymbolsNumber += result;
        }
        return result;
    }

    public int getReadSymbolsNumber() {
        return readSymbolsNumber;
    }

    public void adjustReadSymbolsNumber(int adjustment) {
        readSymbolsNumber += adjustment;
    }
}
