package org.denis.webview.util.io;

import java.io.FilterReader;
import java.io.Reader;

/**
 * {@link Reader} implementation that works on an input stream that contains
 * <a href="http://www.w3schools.com/TAGS/ref_urlencode.asp">url encoded</a> text that in turn may contain
 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">HTML character entities</a> as well as
 * <a href="http://www.w3.org/International/tutorials/tutorial-char-enc/#Slide0430">numeric character references</a>.
 * <p/>
 * Current reader decodes those information to usual unicode java characters on the fly. E.g. consider that we
 * have a mixed English-Russian text which encoded representation looks like
 * {@code 'this+is+a+%26%231090%3B%26%231077%3B%26%231089%3B%26%231090%3B'}. Current reader performs url decoding at
 * first (produces {@code 'this is a &amp;#1090;&amp;#1077;&amp;#1089;&amp;#1090;'}) and html decoding at second that produces
 * {@code 'this is a тест'}.
 *
 * @author Denis Zhdanov
 * @since Jun 26, 2010
 */
public class HtmlReader extends FilterReader {

    public HtmlReader(Reader in) {
        super(new HtmlEntityDecodingReader(new UrlDecodingReader(in)));
    }
}
