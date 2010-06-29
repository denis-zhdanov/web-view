package org.denis.webview.util.io;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author Denis Zhdanov
 * @since 06/27/2010
 */
public class AbstractReplacingFilterReaderTest {
    
    @Test(expected = IllegalArgumentException.class)
    public void nullReaderAtConstructor() {
        new AbstractReplacingFilterReader(null) {
            @Override
            protected int copy(DataContext dataContext) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeInitialBufferSize() {
        getReader("test", -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroInitialBufferSize() {
        getReader("test", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeReadOffset() throws IOException {
        getReader("test").read(new char[0], -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooBigReadOffset() throws IOException {
        getReader("test").read(new char[1], 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeLength() throws IOException {
        getReader("test").read(new char[1], 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroLength() throws IOException {
        getReader("test").read(new char[1], 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooBigReadLength() throws IOException {
        getReader("test").read(new char[1], 0, 2);
    }

    private AbstractReplacingFilterReader getReader(String in) {
        return new AbstractReplacingFilterReader(new StringReader(in), in.length()) {
            @Override
            protected int copy(DataContext dataContext) {
                return 0;
            }
        };
    }

    private AbstractReplacingFilterReader getReader(String in, int initialBufferSize) {
        return new AbstractReplacingFilterReader(new StringReader(in), initialBufferSize) {
            @Override
            protected int copy(DataContext dataContext) {
                return 0;
            }
        };
    }
}
