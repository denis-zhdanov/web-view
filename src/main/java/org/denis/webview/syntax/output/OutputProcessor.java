package org.denis.webview.syntax.output;

import org.denis.webview.syntax.logic.TokenInfo;

import java.io.Writer;
import java.util.Collection;

/**
 * Applies discovered tokens to the target output.
 * <p/>
 * Not thread-safe.
 *
 * @author Denis Zhdanov
 * @since 6/6/11
 */
public class OutputProcessor {

    private final Writer writer;

    public OutputProcessor(Writer writer) {
        this.writer = writer;
    }

    //TODO den add doc
    public void write(char[] data, int start, int end, Collection<TokenInfo> tokens) {

    }
}
