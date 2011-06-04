package org.denis.webview.util.string;

/**
 * {@link AbstractCharSequence} implementation that manages target data at a char array.
 * <p/>
 * The main idea is to use this class as a key during data structures lookups. E.g. there is a possible case
 * that we perform symbol stream's data replacements on the fly, hence, need to check if particular pattern
 * is available. We can wrap our buffer to the object of this class then and examine if there is replacement
 * rule for the current text.
 * <p/>
 * Not thread-safe.
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

    /**
     * Updates current object's state within the given data.
     *
     * @param data      target data array
     * @param start     start offset to use within the given array
     * @param end       end offset to use within the given array
     * @throws IllegalArgumentException     if given array is null or if offsets are invalid/inconsistent
     */
    public void updateState(char[] data, int start, int end) throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Can't process CharArrayCharSequence.updateState(). "
                + "Reason: given 'data' argument is null");
        }
        if (end < start) {
            throw new IllegalArgumentException(String.format("Can't process CharArrayCharSequence.updateState(). "
                + "Reason: given 'end' argument (%d) is less than the 'start' argument (%d)", end, start));
        }
        if (end > data.length) {
            throw new IllegalArgumentException(String.format("Can't process CharArrayCharSequence.updateState(). "
                + "Reason: given offsets([%d; %d)) point beyond the given buffer (length %d)", start, end, data.length));
        }
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
