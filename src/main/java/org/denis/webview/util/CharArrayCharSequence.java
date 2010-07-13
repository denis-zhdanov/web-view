package org.denis.webview.util;

/**
 * {@link AbstractCharSequence} implementation that manages target data at a char array.
 *
 * @author Denis Zhdanov
 * @since Jul 13, 2010
 */
public class CharArrayCharSequence extends AbstractCharSequence {

    private char[] data;
    private int start;
    private int end;

    public CharArrayCharSequence() {
    }

    public CharArrayCharSequence(String s) {
        this(s.toCharArray());
    }

    public CharArrayCharSequence(char[] data) {
        this.data = data;
        this.end = data.length;
    }

    //TODO den add doc
    public void updateState(char[] data, int start, int end) throws IllegalArgumentException {
        //TODO den impl exception processing
        this.data = data;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int index) {
        return data[start + index];
    }
}
