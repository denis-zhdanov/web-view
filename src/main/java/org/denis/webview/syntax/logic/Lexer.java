package org.denis.webview.syntax.logic;

/**
 * Defines general contract for jflex-generated lexers.
 * 
 * @author Denis Zhdanov
 * @since 07.06.11
 */
public interface Lexer {

    TokenType advance() throws java.io.IOException;
    
    int getStartOffset();
    
    int getEndOffset();
}
