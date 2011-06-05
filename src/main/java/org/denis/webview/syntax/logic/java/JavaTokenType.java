package org.denis.webview.syntax.logic.java;

import org.denis.webview.syntax.logic.TokenType;

/**
 * Enumerates interested java token types.
 *
 * @author Denis Zhdanov
 * @since Jul 13, 2010
 */
public enum JavaTokenType implements TokenType {

    // Comments.
    SINGLE_LINE_COMMENT_START(Category.START), MULTI_LINE_COMMENT_START(Category.START);

    private final Category category;

    JavaTokenType(Category category) {
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return category;
    }
}
