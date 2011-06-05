package org.denis.webview.util.io;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.CharBuffer;

import static org.junit.Assert.assertEquals;

/**
 * @author Denis Zhdanov
 * @since 06/04/2011
 */
@RunWith(JMock.class)
public class CharBufferReaderTest {

    private CharBufferReader reader;
    private Mockery mockery;
    private CharBufferReader.Listener listener;

    @Before
    public void setUp() {
        mockery = new JUnit4Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};
        listener = mockery.mock(CharBufferReader.Listener.class);
    }

    @After
    public void checkExpectations() {
        mockery.assertIsSatisfied();
    }

    @Test
    public void emptyBuffer() throws IOException {
        CharBuffer buffer = CharBuffer.allocate(2);
        buffer.position(buffer.limit());
        init(buffer);
        mockery.checking(new Expectations() {{
            exactly(2).of(listener).onBufferEmpty();
        }});

        assertEquals(-1, reader.read());
        assertEquals(-1, reader.read(new char[1]));
    }

    @Test
    public void closePropagation() throws IOException {
        CharBuffer buffer = CharBuffer.allocate(2);
        mockery.checking(new Expectations() {{
            one(listener).close();
        }});
        init(buffer);
        reader.close();
    }

    @Test
    public void readFromDifferentBuffers() throws IOException {
        CharBuffer buffer1 = CharBuffer.allocate(2);
        buffer1.put("ab");
        buffer1.flip();
        init(buffer1);

        final CharBuffer buffer2 = CharBuffer.allocate(2);
        buffer2.put("cd");
        buffer2.flip();
        mockery.checking(new Expectations() {{
            one(listener).onBufferEmpty(); will(new CustomAction("onBufferEmpty") {
                @Override
                public Object invoke(Invocation invocation) throws Throwable {
                    reader.setBuffer(buffer2);
                    return null;
                }
            });
        }});

        char[] buffer = new char[4];
        assertEquals(2, reader.read(buffer));
        assertEquals("ab", new String(buffer, 0, 2));

        assertEquals(2, reader.read(buffer));
        assertEquals("cd", new String(buffer, 0, 2));
    }

    private void init(CharBuffer buffer) {
        reader = new CharBufferReader(buffer, listener);
    }
}
