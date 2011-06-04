package org.denis.webview.util;

import org.denis.webview.util.string.CharArrayCharSequence;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Denis Zhdanov
 * @since 6/2/11
 */
public class CharArrayCharSequenceTest {

    private CharArrayCharSequence charSequence;

    @Before
    public void setUp() {
        charSequence = new CharArrayCharSequence();
    }

    @Test(expected = IllegalArgumentException.class)
    public void undefinedArray() {
        charSequence.updateState(null, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void inconsistentOffsets() {
        charSequence.updateState("123".toCharArray(), 1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidOffsets() {
        charSequence.updateState("123".toCharArray(), 2, 4);
    }
}
