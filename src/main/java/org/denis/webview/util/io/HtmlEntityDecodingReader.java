package org.denis.webview.util.io;

import java.io.FilterReader;
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
public class HtmlEntityDecodingReader extends FilterReader {

    public HtmlEntityDecodingReader(Reader in) {
        super(in);
    }

    /**
     * Reads characters from the wrapped stream into a portion of a given array performing
     * <a href="http://www.w3.org/TR/html4/sgml/entities.html">HTML character entities decoding</a> if necessary.
     *
     * @param buf       destination buffer
     * @param off       offset at which to start storing characters
     * @param len       maximum number of characters to read
     * @return          the number of characters read, or <code>-1</code> if the end of the stream has been reached
     * @throws IllegalArgumentException     if any of the given arguments is invalid or if given buffer
     *                                      contains data that is inconsistent with html entity encoding rules
     * @throws IOException                  in case of unexpected exception during I/O processing
     */
    @Override
    public int read(char[] buf, int off, int len) throws IllegalArgumentException, IOException {
        //TODO den impl
        return 0;
    }
}
