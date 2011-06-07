package org.denis.webview.syntax.logic;

/**
 * @author Denis Zhdanov
 * @since 05.06.11
 */
public interface TokenType {

    final TokenType END_TOKEN = new TokenType() {
        @Override
        public Category getCategory() {
            return Category.END;
        }

        @Override
        public String toString() {
            return "END";
        }
    };

    Category getCategory();

    /**
     * For the performance reasons we may don't want to parse complete token text, e.g. consider that we
     * are interested in string literals. We may define complete string literal token like like the one
     * text surrounded by quotes or we can define two token types - one for string literal start and another
     * one for string literal end.
     * <p/>
     * Current enum allows to differentiate between such toke types.
     */
    enum Category {

        /** Defines token start. */
        START,

        /** Defines token end. */
        END,

        /** Defines complete token. */
        COMPLETE
    }

}
