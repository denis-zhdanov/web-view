package org.denis.webview.util.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Prints all written content to the stdout.
 * 
 * @author Denis Zhdanov
 * @since 6/8/11 4:51 PM
 */
public class DebugWriter extends FilterWriter {

    public DebugWriter(Writer out) {
        super(out);
        System.out.println();
    }

    @Override
    public void write(char[] buf, int off, int len) throws IOException {
        System.out.print(new String(buf, off, len));
        super.write(buf, off, len);
    }
}
