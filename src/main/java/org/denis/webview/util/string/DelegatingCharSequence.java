package org.denis.webview.util.string;

/**
 * {@link AbstractCharSequence} implementation that delegates processing to the wrapped
 * {@link CharSequence} instance.
 *
 * @author Denis Zhdanov
 * @since Jul 13, 2010
 */
public class DelegatingCharSequence extends AbstractCharSequence {

    private CharSequence delegate;

    //TODO den add doc
    public void setDelegate(CharSequence delegate) throws IllegalArgumentException {
        //TODO den throw exception
        this.delegate = delegate;
    }

    @Override
    public int length() {
        return delegate.length();
    }

    @Override
    public char charAt(int index) {
        return delegate.charAt(index);
    }
}
