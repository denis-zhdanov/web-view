package org.denis.webview.syntax.output.markup.inline;

import org.denis.webview.syntax.logic.TokenType;
import org.denis.webview.syntax.output.markup.MarkupScheme;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Markup scheme for output that is highlighted by inline css.
 *
 * @author Denis Zhdanov
 * @since 6/7/11
 */
public class InlineStyleMarkupScheme implements MarkupScheme {

    private final Map<TokenType, char[]> markup
            = new ConcurrentHashMap<TokenType, char[]>();

    public InlineStyleMarkupScheme(StyleScheme styleScheme) {
        for (TokenType tokenType : styleScheme.getSupportedTokenTypes()) {
            StringBuilder buffer = new StringBuilder("style=\"");
            for (StyleRule styleRule : styleScheme.getRules(tokenType)) {
                buffer.append(styleRule.getAttribute().getCssName()).append(":").append(styleRule.getValue())
                    .append(";");
            }
            buffer.append("\"");
            markup.put(tokenType, buffer.toString().toCharArray());
        }
    }

    @Override
    public char[] getMarkup(TokenType tokenType) {
        char[] result = markup.get(tokenType);
        return result == null ? EMPTY_MARKUP : result;
    }
}
