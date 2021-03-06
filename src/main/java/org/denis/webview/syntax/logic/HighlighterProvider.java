package org.denis.webview.syntax.logic;

import org.apache.log4j.Logger;
import org.denis.webview.config.SourceType;
import org.denis.webview.settings.Settings;
import org.denis.webview.util.io.SymbolCountingReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.*;
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

    private static final Logger LOG = Logger.getLogger(HighlighterProvider.class);

    private final ConcurrentMap<SourceType, Class<? extends Lexer>> highlighters
            = new ConcurrentHashMap<SourceType, Class<? extends Lexer>>();

    private Settings settings;
    
    public Highlighter getHighlighter() {
        return new HighlighterImpl(highlighters.get(settings.getSourceType()));
    }

    @Autowired
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        for (SourceType sourceType : SourceType.values()) {
            String name = sourceType.name();
            String className = String.format("org.denis.webview.syntax.logic.%s.%sLexer",
                    name.toLowerCase(), Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase());
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass(className);
                highlighters.put(sourceType, (Class<? extends Lexer>) clazz);
            } catch (Throwable e) {
                highlighters.put(sourceType, EmptyLexer.class);
                LOG.warn(String.format("Can't instantiate lexer for source type '%s' (tried class with name %s)",
                        sourceType, name), e);
            }
        }
    }

    private static class EmptyLexer implements Lexer {

        private final Reader reader;

        public EmptyLexer(Reader reader) {
            this.reader = reader;
        }

        @SuppressWarnings({"StatementWithEmptyBody"})
        @Override
        public List<TokenInfo> advance() throws IOException {
            char[] buffer = new char[256];
            while (reader.read(buffer) >= 0) ;
            return null;
        }
    }
    
    private static class HighlighterImpl implements Highlighter {
        
        private final Set<Listener> listeners = new HashSet<Listener>();
        private final Class<? extends Lexer> lexerClass;

        public HighlighterImpl(Class<? extends Lexer> lexerClass) {
            this.lexerClass = lexerClass;
        }


        @Override
        public void process(SymbolCountingReader reader) throws IOException {
            Lexer lexer;
            try {
                Constructor<? extends Lexer> constructor = lexerClass.getConstructor(Reader.class);
//                lexer = constructor.newInstance(new org.denis.webview.util.io.DebugReader(reader));
                lexer = constructor.newInstance(reader);
            } catch (Exception e) {
                LOG.error(e);
                new HighlighterImpl(EmptyLexer.class).process(reader);
                return;
            }
            int numberOfEndTokensToProvide = 0;
            int lastTokenEndOffset = 0;
            for (List<TokenInfo> tokenInfos = lexer.advance(); tokenInfos != null; tokenInfos = lexer.advance()) {
                for (TokenInfo tokenInfo : tokenInfos) {
                    lastTokenEndOffset = tokenInfo.getEndOffset();
                    for (Listener listener : listeners) {
                        listener.onToken(tokenInfo);
                    }
                    switch (tokenInfo.getTokenType().getCategory()) {
                        case END:
                        case END_LOOK_AHEAD: --numberOfEndTokensToProvide; break;
                        case START:
                            ++numberOfEndTokensToProvide;
                            break;
                        case COMPLETE:
                    }
                }
            }

            // There is a possible case that particular token of category 'end' is not found (e.g. we discovered
            // end-of-line comment start but the input doesn't ends with line feed symbol). We want to provide
            // artificial 'end tokens' then.
            int totalReadSymbolsNumber = reader.getReadSymbolsNumber();
            if (numberOfEndTokensToProvide > 0) {
                TokenInfo tokenInfo = new TokenInfo(TokenType.END_TOKEN, totalReadSymbolsNumber, totalReadSymbolsNumber);
                for (Listener listener : listeners) {
                    for (int i = numberOfEndTokensToProvide; i > 0; i--) {
                        listener.onToken(tokenInfo);
                    }
                }
            } else if (totalReadSymbolsNumber > lastTokenEndOffset) {
                TokenInfo tokenInfo = new TokenInfo(null, lastTokenEndOffset, totalReadSymbolsNumber);
                for (Listener listener : listeners) {
                    listener.onToken(tokenInfo);
                }
            }
        }

        @Override
        public boolean addListener(Listener listener) {
            return listeners.add(listener);
        }

        @Override
        public boolean removeListener(Listener listener) {
            return listeners.remove(listener);
        }
    }
}
