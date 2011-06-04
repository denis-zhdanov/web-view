package org.denis.webview.util.string;

/**
 * Defines {@link #hashCode()} and {@link #equals(Object)} for generic {@link CharSequence} implementation
 * that doesn't check counterparty class. I.e. implementations of different sub-classes of the current class
 * are assumed to be equal if they work with the same sequence of symbols.
 */
public abstract class AbstractCharSequence implements CharSequence {
    @Override
    public int hashCode() {
        int result = 0;
        for (int i = 0, max = Math.min(100, length()); i < max; ++i) {
            result = result * 29 + charAt(i);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CharSequence)) {
            return false;
        }
        CharSequence that = (CharSequence) obj;
        if (length() != that.length()) {
            return false;
        }
        for (int i = 0; i < length(); ++i) {
            if (charAt(i) != that.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return new CharSequence() {
            @Override
            public int length() {
                return end - start;
            }

            @Override
            public char charAt(int index) {
                return AbstractCharSequence.this.charAt(index + start);
            }

            @Override
            public CharSequence subSequence(int s, int e) {
                return AbstractCharSequence.this.subSequence(start + s, end + e);
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < length(); ++i) {
            buffer.append(charAt(i));
        }
        return buffer.toString();
    }
}
