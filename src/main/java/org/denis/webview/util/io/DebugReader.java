package org.denis.webview.util.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Prints all read content to the stdout.
 * 
 * @author Denis Zhdanov
 * @since 6/9/11 5:47 PM
 */
public class DebugReader extends FilterReader {

    public DebugReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        final int result = super.read();
        if (result >= 0) {
            System.out.print((char) result);
        }
        return result;
    }

    @Override
    public int read(char[] buf, int off, int len) throws IOException {
        final int result = super.read(buf, off, len);
        if (result > 0) {
            System.out.print(new String(buf, off, result));
        }
        return result;
    }
}
