package org.denis.webview.syntax.logic;

import org.denis.webview.syntax.logic.Tuple;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;

/**
 * Defines common contract for the strategy that allows to markup target data by specific tokens.
 * <p/>
 * Implementations of this interface are not required to be thread-safe.
 *
 * @author Denis Zhdanov
 * @since Jul 13, 2010
 * @param <T>       target token type
 */
public interface SyntaxHighlightProcessor<T extends Enum<T>> {

    /**
     * Processing characters from the given buffer and produces tokens for it.
     *
     * @param buffer        target data holder
     * @return              //TODO den add doc
     */
    List<Tuple> process(CharBuffer buffer);
}
