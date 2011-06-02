package org.denis.webview.util.io;

import static org.junit.Assert.*;
import org.denis.webview.util.io.HttpParametersReader;
import org.jmock.Expectations;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Zhdanov
 * @since 06/02/2011
 */
@RunWith(JMock.class)
public class HttpParametersReaderTest {

    private Runnable callback;
    private Mockery mockery;
    
    @Before
    public void setUp() {
        mockery = new JUnit4Mockery();
        callback = mockery.mock(Runnable.class);
    }
    
    @After
    public void checkExpectations() {
        mockery.assertIsSatisfied();
    }

    @Test
    public void noCallbackCalls() throws IOException {
        doTest("abc");
    }

    @Test
    public void oneSeparatorInTheMiddle() throws IOException {
        doTest("abc&def");
    }

    @Test
    public void oneSeparatorAtStart() throws IOException {
        doTest("&abc");
    }

    @Test
    public void oneSeparatorAtEnd() throws IOException {
        doTest("abc&");
    }

    private void doTest(String text) throws IOException {
        singleCharTest(text);
        bufferTest(text);
    }

    private void singleCharTest(String text) throws IOException {
        HttpParametersReader reader = new HttpParametersReader(new StringReader(text), callback);
        final int separators = countSeparators(text);
        if (separators > 0) {
            mockery.checking(new Expectations() {{
                exactly(separators).of(callback).run();
            }});
        }
        final String[] expectedChunks = text.split("&");
        for (String chunk : expectedChunks) {
            for (char expected : chunk.toCharArray()) {
                assertEquals(expected, reader.read());
            }
        }
        assertTrue(reader.read() < 0);
    }

    private void bufferTest(String text) throws IOException {
        HttpParametersReader reader = new HttpParametersReader(new StringReader(text), callback);
        final int separators = countSeparators(text);
        if (separators > 0) {
            mockery.checking(new Expectations() {{
                exactly(separators).of(callback).run();
            }});
        }
        final String[] expectedChunks = text.split("&");
        char[] buffer = new char[text.length()];
        for (String chunk : expectedChunks) {
            int read = reader.read(buffer);
            assertEquals(chunk, new String(buffer, 0, read));
        }
    }

    private static int countSeparators(String text) {
        int result = 0;
        for (char c : text.toCharArray()) {
            if (c == '&') {
                ++result;
            }
        }
        return result;
    }
}
