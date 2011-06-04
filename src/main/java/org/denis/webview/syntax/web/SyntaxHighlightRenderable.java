package org.denis.webview.syntax.web;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.Renderable;
import org.denis.webview.config.Ide;
import org.denis.webview.config.SourceType;
import org.denis.webview.util.string.CharArrayCharSequence;
import org.denis.webview.util.io.HtmlEntityDecodingReader;
import org.denis.webview.util.io.HttpParametersReader;
import org.denis.webview.util.io.UrlDecodingReader;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
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

    private static final String SOURCE_PARAMETER_NAME = "source";

    /** Buffer used during syntax highlighting processing. */
    private final CharBuffer readerBuffer = CharBuffer.allocate(1024);

    /** Buffer used during highlighting parameters parsing. */
    private final CharBuffer paramsBuffer = CharBuffer.allocate(32);

    private Reader reader;
    private boolean newParamStarted;


    @Override
    public boolean render(InternalContextAdapter context, Writer writer)
        throws IllegalArgumentException, IOException {
        // Parse highlighting parameters.
        Map<Class<?>, Object> params = parseParams();

        // Parse tokens.
        //TODO den impl

        return false;
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

    enum Target { KEY, VALUE }
    @SuppressWarnings({"unchecked", "EqualsBetweenInconvertibleTypes"})
    private Map<Class<?>, Object> parseParams() throws IOException, IllegalArgumentException {
        Map<Class<?>, Object> params = getParamsHolder();
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
}
