package org.denis.webview.util.io;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;

/**
 * @author Denis Zhdanov
 * @since 06/27/2010
 */
@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public abstract class AbstractReplacingFilterReaderTest<T extends AbstractReplacingFilterReader> {

    private final ReaderProvider<T> readerProvider;

    public AbstractReplacingFilterReaderTest(Class<T> clazz) {
        this(new ReflectionBasedReaderProvider<T>(clazz));
    }

    public AbstractReplacingFilterReaderTest(ReaderProvider<T> readerProvider) {
        this.readerProvider = readerProvider;
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullReaderAtConstructor() {
        new AbstractReplacingFilterReader(null, 1) {
            @Override
            protected int copy(DataContext dataContext) {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeMaxReplacementValue() throws Exception {
        new AbstractReplacingFilterReader(new StringReader("test"), -1) {
            @Override
            protected int copy(DataContext dataContext) throws IllegalStateException {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroMaxReplacementValue() throws Exception {
        new AbstractReplacingFilterReader(new StringReader("test"), 0) {
            @Override
            protected int copy(DataContext dataContext) throws IllegalStateException {
                return 0;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeReadOffset() throws Exception {
        getReader("test").read(new char[0], -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooBigReadOffset() throws Exception {
        getReader("test").read(new char[1], 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeLength() throws Exception {
        getReader("test").read(new char[1], 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroLength() throws Exception {
        getReader("test").read(new char[1], 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooBigReadLength() throws Exception {
        getReader("test").read(new char[1], 0, 2);
    }

    protected T getReader(String in) throws Exception {
        return readerProvider.getReader(in);
    }

    protected void doTest(String in, String out) throws Exception {
        testWithSingleCharRead(in, out);
        testWithSmallBuffer(in, out);
        testWithBigBuffer(in, out);
    }

    private void testWithSingleCharRead(String in, String out) throws Exception {
        T reader = getReader(in);
        StringBuilder builder = new StringBuilder();
        int read;
        while ((read = reader.read()) >= 0) {
            builder.append((char)read);
        }
        assertEquals(out, builder.toString());
    }

    private void testWithSmallBuffer(String in, String out) throws Exception {
        T reader = getReader(in);
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[2];
        int read;
        while ((read = reader.read(buffer)) >= 0) {
            builder.append(new String(buffer, 0, read));
        }
        assertEquals(out, builder.toString());
    }

    private void testWithBigBuffer(String in, String out) throws Exception {
        T reader = getReader(in);
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[1024];
        int read;
        while ((read = reader.read(buffer)) >= 0) {
            builder.append(new String(buffer, 0, read));
        }
        assertEquals(out, builder.toString());
    }


    public interface ReaderProvider<T extends AbstractReplacingFilterReader> {
        T getReader(String in) throws Exception;
    }

    private static class ReflectionBasedReaderProvider<T extends AbstractReplacingFilterReader>
        implements ReaderProvider<T>
    {

        private final Class<T> clazz;

        private ReflectionBasedReaderProvider(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T getReader(String in) throws Exception {
            Constructor<T> constructor = clazz.getConstructor(Reader.class);
            return constructor.newInstance(new StringReader(in));
        }
    }
}
