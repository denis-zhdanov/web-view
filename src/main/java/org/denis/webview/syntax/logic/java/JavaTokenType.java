package org.denis.webview.syntax.logic.java;

/**
 * Enumerates possible java token types that need to be highlighted.
 *
 * @author Denis Zhdanov
 * @since Jul 13, 2010
 */
public enum JavaTokenType {
    KEYWORD, STRING_LITERAL, CHAR_LITERAL, NUMBER_LITERAL, COMMENT, JAVADOC, JAVADOC_TAG, ANNOTATION,
    STATIC_FIELD, INSTANCE_FIELD, STATIC_METHOD_CALL, TODO, HTML_TAG
}
