package org.denis.webview.syntax.output.markup;

import org.denis.webview.syntax.logic.TokenType;

/**
 * Defines common interface to retrieve markup data for the target token type.
 * <p/>
 * Implementations of this interface are assumed to be thread-safe.
 *
 * @author Denis Zhdanov
 * @since 6/7/11
 */
public interface MarkupScheme {

    char[] EMPTY_MARKUP = new char[0];

    /**
     * Allows to retrieve markup data for the given token type.
     *
     * @param tokenType     target token type
     * @return              markup data for the given token type if any; empty array otherwise
     */
    char[] getMarkup(TokenType tokenType);
}
