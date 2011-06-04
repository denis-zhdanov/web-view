package org.denis.webview.syntax.logic;

import java.io.Reader;

/**
 * Defines common contract for the algorithm that traverses given symbol stream and notifies interested clients
 * about discovered tokens.
 * 
 * @author Denis Zhdanov
 * @since 6/3/11 6:26 PM
 * @param <T>   token type
 */
public interface Highlighter<T> {

    /**
     * Processes given symbol stream and notifies all {@link #addListener(Listener) registered listeners}
     * about discovered tokens.
     * 
     * @param reader    target data provider
     */
    void process(Reader reader);

    /**
     * Registers given listener within the current highlighter.
     * 
     * @param listener    listener to register
     * @return            <code>true</code> if no such listener was registered before; <code>false</code> otherwise
     */
    boolean addListener(Listener<T> listener);
    
    interface Listener<C> {
        void onToken(TokenInfo<C> info);
    }
}
