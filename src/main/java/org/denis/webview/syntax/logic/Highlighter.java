package org.denis.webview.syntax.logic;

import org.denis.webview.util.io.SymbolCountingReader;

import java.io.IOException;
import java.io.Reader;

/**
 * Defines common contract for the algorithm that traverses given symbol stream and notifies interested clients
 * about discovered tokens.
 * 
 * @author Denis Zhdanov
 * @since 6/3/11 6:26 PM
 */
public interface Highlighter {

    /**
     * Processes given symbol stream and notifies all {@link #addListener(Listener) registered listeners}
     * about discovered tokens.
     * 
     * @param reader    target data provider
     * @throws IOException      in case of unexpected I/O problem with the given symbol stream
     */
    void process(SymbolCountingReader reader) throws IOException;

    /**
     * Registers given listener within the current highlighter.
     * 
     * @param listener    listener to register
     * @return            <code>true</code> if no such listener was registered before; <code>false</code> otherwise
     */
    boolean addListener(Listener listener);

    interface Listener {
        void onToken(TokenInfo info);
    }
}
