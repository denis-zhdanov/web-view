package org.denis.webview.syntax.logic;

import org.apache.log4j.Logger;
import org.denis.webview.config.SourceType;
import org.denis.webview.syntax.logic.java.JavaTokenType;
import org.denis.webview.util.io.SymbolCountingReader;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests highlighters against available test data.
 * 
 * @author Denis Zhdanov
 * @since 6/8/11 10:53 AM
 */
public class HighlighterTest {

    public static final String TOKEN_START_PREFIX = "<%";
    public static final String TOKEN_START_SUFFIX = ">";
    public static final String TOKEN_END = "<%>";
    
    Logger LOG = Logger.getLogger(HighlighterTest.class);
    
    private static final Map<String, TokenType> TOKEN_TYPES = new HashMap<String, TokenType>();
    static {
        register(TokenType.END_TOKEN);
        register(JavaTokenType.values());
    }
    
    
    private static void register(TokenType ... tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            TOKEN_TYPES.put(tokenType.toString(), tokenType);
        }
    }

    private final StringBuilder   rawText    = new StringBuilder();
    private final List<TokenInfo> tokenInfos = new ArrayList<TokenInfo>();

    @Test
    public void suit() throws IOException {
        HighlighterProvider highlighterProvider = new HighlighterProvider();
        highlighterProvider.init();
        for (SourceType sourceType : SourceType.values()) {
            doTest(sourceType, highlighterProvider.getHighlighter(sourceType));
        }
    }

    @SuppressWarnings("unchecked")
    private void doTest(SourceType sourceType, Highlighter highlighter) throws IOException {
        
        // Discover test data.
        String packageEntry = sourceType.toString().toLowerCase();
        final String classPathRootPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        final String packagePath = getClass().getPackage().getName().replace(".", "/");
        File testDataDir = new File(String.format("%s/%s/%s", classPathRootPath, packagePath, packageEntry));
        if (!testDataDir.isDirectory()) {
            LOG.warn(String.format(
                    "No test data is found for source type %s (checked the following dir - '%s')",
                    sourceType, testDataDir.getAbsolutePath()
            ));
            return;
        }
        
        for (File file : testDataDir.listFiles()) {
            final String message = String.format("'%s'", file.getName());
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            char[] charBuffer = new char[1024];
            int read = 0;
            for (; read >= 0; read = reader.read(charBuffer)) {
                buffer.append(new String(charBuffer, 0, read));
            }
            reader.close();
            
            prepare(buffer.toString());
            final List<TokenInfo> expected = getTokenInfos();
            Highlighter.Listener listener = new Highlighter.Listener() {
                @Override
                public void onToken(TokenInfo info) {
                    assertFalse(message, expected.isEmpty());
                    assertEquals(message, expected.remove(0), info);
                }
            };
            assertTrue(message, highlighter.addListener(listener));
            try {
                highlighter.process(new SymbolCountingReader(new StringReader(getRawText())));
            } finally {
                assertTrue(message, highlighter.removeListener(listener));
            }
            assertTrue(message, expected.isEmpty());
        }
    }
    
    private enum State { SEARCH_START, PARSE_TOKEN, SEARCH_END }
    /**
     * Parses given text with token info markup and exposes the result via {@link #getRawText()} and
     * {@link #getTokenInfos()}.
     *
     * @param metaText    text with token info markup
     * @throws IllegalArgumentException     if given text contains invalid markup
     */
    public void prepare(String metaText) throws IllegalArgumentException {
        rawText.setLength(0);
        tokenInfos.clear();
        
        State state = State.SEARCH_START;
        int metaTextOffset = 0;
        int rawTextOffset = 0;
        TokenType tokenType = null;
        int tokenStartOffset = 0;
        while (metaTextOffset < metaText.length()) {
            switch (state) {
                case SEARCH_START:
                    int i = metaText.indexOf(TOKEN_START_PREFIX, metaTextOffset);
                    if (i < 0) {
                        rawText.append(metaText.substring(metaTextOffset));
                        return;
                    }
                    rawText.append(metaText.substring(metaTextOffset, i));
                    state = State.PARSE_TOKEN;
                    rawTextOffset += i - metaTextOffset;
                    tokenStartOffset = rawTextOffset;
                    metaTextOffset = i + TOKEN_START_PREFIX.length();
                    break;
                case PARSE_TOKEN:
                    int j = metaText.indexOf(TOKEN_START_SUFFIX, metaTextOffset);
                    if (j < 0) {
                        throw new IllegalArgumentException(String.format(
                                "Invalid text detected. Expected to find token start suffix (%s) after offset %d "
                                 + "but it has not been found. Text: '%s'",
                                TOKEN_START_SUFFIX, metaTextOffset, metaText
                        ));
                    }
                    boolean shouldSearchEnd = true;
                    if (metaText.charAt(j - 1) == '%') {
                        shouldSearchEnd = false;
                        --j;
                    }
                    String tokenString = metaText.substring(metaTextOffset, j);
                    tokenType = TOKEN_TYPES.get(tokenString);
                    if (tokenType == null) {
                        throw new IllegalArgumentException(String.format(
                                "Invalid token type detected - '%s'. Known types: %s. Text: '%s'",
                                tokenString, TOKEN_TYPES.keySet(), metaText
                        ));
                    }

                    if (shouldSearchEnd) {
                        metaTextOffset = j + TOKEN_START_SUFFIX.length();
                        state = State.SEARCH_END;
                    } else {
                        tokenInfos.add(new TokenInfo(tokenType, tokenStartOffset, tokenStartOffset));
                        state = State.SEARCH_START;
                        metaTextOffset = j + TOKEN_START_SUFFIX.length() + 1;
                    }
                    break;
                case SEARCH_END:
                    int k = metaText.indexOf(TOKEN_END, metaTextOffset);
                    if (k < 0) {
                        throw new IllegalArgumentException(String.format(
                                "Invalid text detected. Expected to find token end mark (%s) after offset %d." +
                                        "Text: '%s'", TOKEN_END, metaTextOffset, metaText
                        ));
                    }
                    rawText.append(metaText.substring(metaTextOffset, k));
                    rawTextOffset += k - metaTextOffset;
                    tokenInfos.add(new TokenInfo(tokenType, tokenStartOffset, rawTextOffset));
                    metaTextOffset = k + TOKEN_END.length();
                    state = State.SEARCH_START;
            }
        }
    }

    /**
     * @return  {@link #prepare(String) client text}  without markup info
     */
    public String getRawText() {
        return rawText.toString();
    }

    /**
     * @return  parsed markup info if any; empty collection otherwise
     */
    public List<TokenInfo> getTokenInfos() {
        return tokenInfos;
    }
}
