package org.denis.webview.syntax.logic;

/**
 * Encapsulates information about discovered token.
 * 
 * @author Denis Zhdanov
 * @since 6/3/11 6:25 PM
 */
public class TokenInfo {

    private final TokenType tokenType;
    private final int startOffset;
    private final int endOffset;

    public TokenInfo(TokenType tokenType, int startOffset, int endOffset) {
        this.tokenType = tokenType;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    @Override
    public String toString() {
        return tokenType + ": " + startOffset + "-" + endOffset;
    }
}