package org.denis.webview.syntax.logic;

import org.apache.log4j.Logger;
import org.denis.webview.config.SourceType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 'Glue' class between the application spring-infrastructure and generated lexers.
 * <p/>
 * Thread-safe.
 *
 * @author Denis Zhdanov
 * @since 05.06.11
 */
@Component
public class HighlighterProvider {

    private static final Highlighter EMPTY_HIGHLIGHTER = new Highlighter() {
        @SuppressWarnings({"StatementWithEmptyBody"})
        @Override
        public void process(Reader reader) throws IOException {
            char[] buffer = new char[256];
            while (reader.read(buffer) >= 0) ;
        }

        @Override
        public boolean addListener(Listener listener) {
            return true;
        }
    };

    private static final Logger LOG = Logger.getLogger(HighlighterProvider.class);

    private final ConcurrentMap<SourceType, Highlighter> highlighters
            = new ConcurrentHashMap<SourceType, Highlighter>();

    public Highlighter getHighlighter(SourceType sourceType) {
        return highlighters.get(sourceType);
    }

    @PostConstruct
    public void init() {
        for (SourceType sourceType : SourceType.values()) {
            String name = sourceType.name();
            String className = String.format("org.denis.webview.syntax.logic.%s.%sLexer",
                    name.toLowerCase(), Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase());
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass(className);
                highlighters.put(sourceType, (Highlighter) clazz.newInstance());
            } catch (Throwable e) {
                highlighters.put(sourceType, EMPTY_HIGHLIGHTER);
                LOG.warn(String.format("Can't instantiate lexer for source type '%s' (tried class with name %s)",
                        sourceType, name), e);
            }
        }
    }
}
