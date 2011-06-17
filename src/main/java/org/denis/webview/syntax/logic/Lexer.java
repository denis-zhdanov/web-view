package org.denis.webview.syntax.logic;

import java.util.List;

/**
 * Defines general contract for jflex-generated lexers.
 * 
 * @author Denis Zhdanov
 * @since 07.06.11
 */
public interface Lexer {

    List<TokenInfo> advance() throws java.io.IOException;
}
