package org.denis.webview.util.io;

import org.junit.Test;

/**
 * @author Denis Zhdanov
 * @since 06/27/2010
 */
public class UrlDecodingReaderTest extends AbstractReplacingFilterReaderTest<UrlDecodingReader> {

    public UrlDecodingReaderTest() {
        super(UrlDecodingReader.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidHexSymbols() throws Exception {
        getReader("%AG").read(new char[5]);
    }

    @Test(expected = IllegalStateException.class)
    public void incompleteHexData() throws Exception {
        UrlDecodingReader reader = getReader("%A");
        reader.read(new char[5]);

        // We perform the reading second time in order to get EOF indication.
        reader.read(new char[5]);
    }

    @Test
    public void noReplacements() throws Exception {
        doTest("test", "test");
    }

    @Test
    public void spaceReplacement() throws Exception {
        doTest("this+is+a+test", "this is a test");
    }

    @Test
    public void hexReplacement() throws Exception {
        doTest("%26%231090%3B%26%231077%3B%26%231089%3B%26%231090%3B", "&#1090;&#1077;&#1089;&#1090;");
    }

    @Test
    public void mixedText() throws Exception {
        doTest(
            "this+is+a+%26%231090%3B%26%231077%3B%26%231089%3B%26%231090%3B",
            "this is a &#1090;&#1077;&#1089;&#1090;"
        );
    }
}
