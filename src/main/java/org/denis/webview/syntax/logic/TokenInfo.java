package org.denis.webview.syntax.logic;

/**
 * Encapsulates information about discovered token.
 * 
 * @author Denis Zhdanov
 * @since 6/3/11 6:25 PM
 * @param <T>    token type
 */
public interface TokenInfo<T> {

    T getType();
    
    int getStartOffset();

    int getEndOffset();
}