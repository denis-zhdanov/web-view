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
    public int hashCode() {
        int result = tokenType != null ? tokenType.hashCode() : 0;
        result = 31 * result + startOffset;
        result = 31 * result + endOffset;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenInfo that = (TokenInfo) o;
        return tokenType == that.tokenType && startOffset == that.startOffset && endOffset == that.endOffset;
    }

    @Override
    public String toString() {
        return tokenType + ": " + startOffset + "-" + endOffset;
    }
}