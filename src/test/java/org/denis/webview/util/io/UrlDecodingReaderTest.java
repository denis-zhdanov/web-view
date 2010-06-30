package org.denis.webview.util.io;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author Denis Zhdanov
 * @since 06/27/2010
 */
public class UrlDecodingReaderTest {

    @Test(expected = IllegalArgumentException.class)
    public void invalidHexSymbols() throws IOException {
        getReader("%AG").read(new char[5]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void incompleteHexData() throws IOException {
        UrlDecodingReader reader = getReader("%A");
        reader.read(new char[5]);

        // We perform the reading second time in order to get EOF indication.
        reader.read(new char[5]);
    }

    @Test
    public void noReplacements() throws IOException {
        doTest("test", "test");
    }

    @Test
    public void spaceReplacement() throws IOException {
        doTest("this+is+a+test", "this is a test");
    }

    @Test
    public void hexReplacement() throws IOException {
        doTest("%26%231090%3B%26%231077%3B%26%231089%3B%26%231090%3B", "&#1090;&#1077;&#1089;&#1090;");
    }

    @Test
    public void mixedText() throws IOException {
        doTest(
            "this+is+a+%26%231090%3B%26%231077%3B%26%231089%3B%26%231090%3B",
            "this is a &#1090;&#1077;&#1089;&#1090;"
        );
    }

    private UrlDecodingReader getReader(String in) {
        return new UrlDecodingReader(new StringReader(in));
    }

    private void doTest(String in, String out) throws IOException {
        UrlDecodingReader reader = getReader(in);
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[1024];
        int read;
        while ((read = reader.read(buffer)) >= 0) {
            builder.append(new String(buffer, 0, read));
        }
        assertEquals(out, builder.toString());
    }
}
