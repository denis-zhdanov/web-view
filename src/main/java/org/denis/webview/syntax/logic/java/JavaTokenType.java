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
    SINGLE_LINE_COMMENT_START, MULTI_LINE_COMMENT_START, JAVADOC_START, JAVADOC_TAG_START, JAVADOC_HTML_TAG_START,
    TODO_COMMENT_START,
    
    // Strings.
    STRING_LITERAL_START, CHAR_LITERAL(Category.COMPLETE),
    
    KEYWORD(Category.COMPLETE), ANNOTATION_START;

    public static final JavaTokenType[] COMMENTS = {
            SINGLE_LINE_COMMENT_START, MULTI_LINE_COMMENT_START, JAVADOC_START
    };
    
    private final Category category;

    JavaTokenType() {
        this(Category.START);
    }

    JavaTokenType(Category category) {
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return category;
    }
}
