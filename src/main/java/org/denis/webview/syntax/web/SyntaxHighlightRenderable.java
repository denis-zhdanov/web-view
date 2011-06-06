package org.denis.webview.syntax.web;

import org.apache.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.Renderable;
import org.denis.webview.config.Ide;
import org.denis.webview.config.SourceType;
import org.denis.webview.syntax.logic.Highlighter;
import org.denis.webview.syntax.logic.HighlighterProvider;
import org.denis.webview.syntax.logic.TokenInfo;
import org.denis.webview.syntax.output.OutputProcessor;
import org.denis.webview.util.io.CharBufferReader;
import org.denis.webview.util.string.CharArrayCharSequence;
import org.denis.webview.util.io.HtmlEntityDecodingReader;
import org.denis.webview.util.io.HttpParametersReader;
import org.denis.webview.util.io.UrlDecodingReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter of application-specific syntax highlighting logic to <code>Velocity</code> API.
 * <p/>
 * Not thread-safe.
 *
 * @author Denis Zhdanov
 * @since Jun 24, 2010
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST,  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SyntaxHighlightRenderable implements Renderable {

    private static final Map<CharSequence, Class<?>> REQUEST_PARAMETER_NAMES = new HashMap<CharSequence, Class<?>>();
    private static final Map<Class<?>, Map<String, Object>> ENUM_MEMBERS = new HashMap<Class<?>, Map<String, Object>>();
    static {
        register("ide", Ide.class, Ide.values());
        register("language", SourceType.class, SourceType.values());
    }

    private static <T extends Enum<T>> void register(String requestParamName, Class<T> enumClass, T[] values) {
        REQUEST_PARAMETER_NAMES.put(new CharArrayCharSequence(requestParamName), enumClass);

        Map<String, Object> map = new HashMap<String, Object>();
        for (T value : values) {
            map.put(value.toString(), value);
        }
        ENUM_MEMBERS.put(enumClass, map);
    }

    Logger LOG = Logger.getLogger(SyntaxHighlightRenderable.class);
    private static final String SOURCE_PARAMETER_NAME = "source";
    private static final int    BUFFER_SIZE           = 1024;

    /**
     * Buffers used during syntax highlighting processing.
     * <p/>
     * We use two buffers here in order to avoid situation when particular token doesn't fit single buffer,
     * e.g. it's start is contained at the buffer but the end is not (e.g. let get keyword 'private'. There
     * is a possible case that we read 'pri' at the buffer only).
     */
    private final ReadData readerData1 = new ReadData();
    private final ReadData readerData2 = new ReadData();

    /** Buffer used during highlighting parameters parsing. */
    private final CharBuffer paramsBuffer = CharBuffer.allocate(32);

    private HighlighterProvider highlighterProvider;
    private Reader reader;
    private boolean newParamStarted;


    @Override
    public boolean render(InternalContextAdapter context, Writer writer)
        throws IllegalArgumentException, IOException {
        // Parse highlighting parameters.
        Map<Class<?>, Object> params = parseParams();

        // Setup output processor.
        OutputProcessor outputProcessor = new OutputProcessor(writer);

        // Setup rolling input symbol stream.
        CharBufferListener readerListener = new CharBufferListener(outputProcessor);
        CharBufferReader charBufferReader = new CharBufferReader(readerData1.buffer, readerListener);
        readerListener.setReader(charBufferReader);

        // Parse tokens.
        Highlighter highlighter = highlighterProvider.getHighlighter((SourceType) params.get(SourceType.class));
        highlighter.addListener(new HighlighterListener(outputProcessor));
        highlighter.process(charBufferReader);

        return true;
    }

    public void setReader(Reader reader) {
        this.reader = new HtmlEntityDecodingReader(new UrlDecodingReader(new HttpParametersReader(
            reader,
            new Runnable() {
                @Override
                public void run() {
                    newParamStarted = true;
                }
            }
        )));
    }

    @Autowired
    public void setHighlighterProvider(HighlighterProvider highlighterProvider) {
        this.highlighterProvider = highlighterProvider;
    }

    private enum Target { KEY, VALUE }
    @SuppressWarnings({"unchecked", "EqualsBetweenInconvertibleTypes"})
    private Map<Class<?>, Object> parseParams() throws IOException, IllegalArgumentException {
        Map<Class<?>, Object> params = getParamsHolder();
        CharBuffer readerBuffer = readerData1.buffer;
        readerBuffer.clear();
        paramsBuffer.clear();
        int read;
        Target target = Target.KEY;
        Class<?> currentKey = null;
        CharArrayCharSequence mapKey = new CharArrayCharSequence();
        while (reader.read(readerBuffer) >= 0) {
            readerBuffer.flip();
            while (readerBuffer.hasRemaining()) {
                char c = readerBuffer.get();
                switch (target) {
                    case KEY:
                        if (c == '=') {
                            mapKey.updateState(paramsBuffer.array(), 0, paramsBuffer.position());
                            if (mapKey.equals(SOURCE_PARAMETER_NAME)) {
                                return params;
                            }
                            currentKey = REQUEST_PARAMETER_NAMES.get(mapKey);
                            target = Target.VALUE;
                            paramsBuffer.clear();
                        } else {
                            paramsBuffer.put(c);
                        }
                        break;
                    case VALUE:
                        paramsBuffer.put(c);
                }
            }
            readerBuffer.clear();
            if (newParamStarted) {
                newParamStarted = false;
                if (currentKey != null) {
                    String valueAsString = new String(paramsBuffer.array(), 0, paramsBuffer.position()).toUpperCase();
                    Map<String, Object> map = ENUM_MEMBERS.get(currentKey);
                    Object value = map.get(valueAsString);
                    if (value != null) {
                        params.put(currentKey, value);
                    }
                }

                paramsBuffer.clear();
                target = Target.KEY;
            }
        }
        return params;
    }

    private static Map<Class<?>, Object> getParamsHolder() {
        Map<Class<?>, Object> result = new HashMap<Class<?>, Object>();
        result.put(Ide.class, Ide.IDEA);
        result.put(SourceType.class, SourceType.JAVA);
        return result;
    }

    private class CharBufferListener implements CharBufferReader.Listener {

        private final OutputProcessor outputProcessor;

        private CharBufferReader charBufferReader;

        CharBufferListener(OutputProcessor outputProcessor) {
            this.outputProcessor = outputProcessor;
        }

        public void setReader(CharBufferReader charBufferReader) {
            this.charBufferReader = charBufferReader;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public void onBufferEmpty() {
            try {
                readMoreData();
            } catch (IOException e) {
                LOG.warn("Unexpected I/O exception occurred during the processing", e);
                try {
                    reader.close();
                } catch (IOException e1) {
                    // Ignore
                }
            }
        }

        private void readMoreData() throws IOException {
            ReadData newData = charBufferReader.getBuffer() == readerData1.buffer ? readerData2 : readerData1;

            // Flush all data from the buffer if it has the one.
            if (!newData.isEmpty()) {
                outputProcessor.write(
                    newData.buffer.array(), newData.bufferStart, newData.bufferEnd,
                    Collections.<TokenInfo>emptyList()
                );
            }

            newData.buffer.clear();
            ReadData currentData = newData == readerData1 ? readerData2 : readerData1;
            newData.readSymbols = reader.read(newData.buffer);
            newData.bufferStart = 0;
            newData.bufferEnd = newData.readSymbols;
            newData.clientShift = currentData.clientShift + currentData.readSymbols;
            charBufferReader.setBuffer(newData.buffer);
        }
    }

    private class HighlighterListener implements Highlighter.Listener {

        private final OutputProcessor outputProcessor;

        public HighlighterListener(OutputProcessor outputProcessor) {
            this.outputProcessor = outputProcessor;
        }

        @Override
        public void onToken(TokenInfo<?> info) {
            //TODO den impl
        }
    }

    private static class ReadData {

        /** Target buffer. */
        public final CharBuffer buffer = CharBuffer.allocate(BUFFER_SIZE);

        /** Offset of the first symbol of the current buffer that is not written to the output yet. */
        public int bufferStart;

        /** Offset beyond the last symbol of the current buffer that is not written to the output yet. */
        public int bufferEnd;

        /**
         * Value to add to the {@link #bufferStart} in order to get offset for the whole client input.
         */
        public int clientShift;


        /** Number of client input symbols read to the current data. */
        public int readSymbols;

        public boolean isEmpty() {
            return bufferEnd > bufferStart;
        }
    }
}
