package org.denis.webview.syntax.web;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.Renderable;
import org.denis.webview.config.Ide;
import org.denis.webview.config.SourceType;
import org.denis.webview.syntax.logic.SyntaxHighlightProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class SyntaxHighlightRenderable implements Renderable {

    private static final Map<CharSequence, Class<?>> REQUEST_PARAMETER_NAMES = new HashMap<CharSequence, Class<?>>();
    static {
        REQUEST_PARAMETER_NAMES.put("ide", Ide.class);
        REQUEST_PARAMETER_NAMES.put("language", SourceType.class);
    }
    private static final String SOURCE_PARAMETER_NAME = "source";

    /** Buffer used during syntax highlighting processing. */
    private final CharBuffer readerBuffer = CharBuffer.allocate(1024);

    /** Buffer used during highlighting parameters parsing. */
    private final CharBuffer paramsBuffer = CharBuffer.allocate(32);

    private Reader reader;
    private SyntaxHighlightProcessor highlightRequestProcessor;

    @Override
    public boolean render(InternalContextAdapter context, Writer writer)
        throws IllegalArgumentException, IOException {
        // Parse highlighting parameters.
        Map<Class<?>, Object> params = parseParams();

        // Parse tokens.


        return false;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    @Autowired
    public void setHighlightRequestProcessor(SyntaxHighlightProcessor processor) {
        highlightRequestProcessor = processor;
    }

    enum Target { KEY, EQ_SIGN, VALUE }
    private Map<Class<?>, Object> parseParams() throws IOException, IllegalArgumentException {
        Map<Class<?>, Object> params = getParamsHolder();
        readerBuffer.clear();
        paramsBuffer.clear();
        int read;
        Target target = Target.KEY;
        while ((read = reader.read(readerBuffer)) >= 0) {
            while (--read >= 0) {
                switch (target) {
                    case KEY:
                        char c = readerBuffer.get();
                        if (c == '=') {
                            //TODO den impl
                        } else {
                            paramsBuffer.put(c);
                        }
                        break;
                }
            }
        }
    }

    private static Map<Class<?>, Object> getParamsHolder() {
        Map<Class<?>, Object> result = new HashMap<Class<?>, Object>();
        result.put(Ide.class, Ide.IDEA);
        result.put(SourceType.class, null);
        return result;
    }

    private static void validateHighlightParams(Map<Class<?>, Object> params) throws IllegalArgumentException {
        //TODO den impl
    }
}
