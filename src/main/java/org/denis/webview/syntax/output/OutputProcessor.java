package org.denis.webview.syntax.output;

import org.denis.webview.syntax.logic.TokenInfo;
import org.denis.webview.syntax.logic.TokenType;
import org.denis.webview.syntax.output.markup.MarkupScheme;

import java.io.IOException;
import java.io.Writer;

/**
 * Applies discovered tokens to the target output.
 * <p/>
 * Not thread-safe.
 *
 * @author Denis Zhdanov
 * @since 6/6/11
 */
public class OutputProcessor {

    private static final char[] START_TOKEN_PREFIX = "<span ".toCharArray();
    private static final char[] START_TOKEN_SUFFIX = ">".toCharArray();
    private static final char[] END_TOKEN_MARK = "</span>".toCharArray();

    private final Writer       writer;
    private final MarkupScheme markupScheme;

    public OutputProcessor(Writer writer, MarkupScheme markupScheme) {
        this.writer = writer;
        this.markupScheme = markupScheme;
    }

    /**
     * Asks to write given data to the output possibly indicating that it is of particular token type.
     * 
     * @param data     target data holder
     * @param start    start offset of the target data within the given buffer
     * @param end      end offset of the target data within the given buffer
     * @param token    target data type if defined (not null)
     */
    public void write(char[] data, int start, int end, TokenInfo token) {
        try {
            doWrite(data, start, end, token == null ? null : token.getTokenType());
        } catch (IOException e) {
            // Ignore
        }
    }

    private void doWrite(char[] data, int start, int end, TokenType tokenType) throws IOException {
        if (tokenType == null) {
            writer.write(data, start, end - start);
            return;
        }

        if (tokenType.getCategory() == TokenType.Category.END) {
            writer.write(data, start, end - start);
            writer.write(END_TOKEN_MARK);
            return;
        }

        char[] markup = markupScheme.getMarkup(tokenType);
        if (markup.length <= 0) {
            writer.write(data, start, end - start);
            return;
        }

        writer.write(START_TOKEN_PREFIX);
        writer.write(markup);
        writer.write(START_TOKEN_SUFFIX);
        writer.write(data, start, end - start);

        if (tokenType.getCategory() == TokenType.Category.COMPLETE) {
            writer.write(END_TOKEN_MARK);
        }
    }
}
