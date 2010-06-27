package org.denis.webview.util.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Symbol stream that provides transparent decoding of
 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">HTML character entities</a> and
 * <a href="http://www.w3.org/International/tutorials/tutorial-char-enc/#Slide0430">numeric character references</a>
 * from underlying stream.
 *
 * @author Denis Zhdanov
 * @since Jun 27, 2010
 */
public class HtmlEntityDecodingReader extends Reader {

    private final Reader in;

    public HtmlEntityDecodingReader(Reader in) {
        this.in = in;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        //TODO den impl
        return 0;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
