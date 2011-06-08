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

    final TokenType END_LOOK_AHEAD_TOKEN = new TokenType() {
        @Override
        public Category getCategory() {
            return Category.END_LOOK_AHEAD;
        }

        @Override
        public String toString() {
            return "END_LOOK_AHEAD";
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

        /**
         * Defines token end where client is not interested at end offset.
         * <p/>
         * For example we may want to match javadoc tag and discover its start by token of {@link #START} type.
         * Then it's necessary to find its end like the offset of the first non-symbol-digit symbol.
         * Resulting rule will be something like [^\d\w], i.e. it will match, for example, white space. However,
         * we don't want to apply the javadoc token rule to that white space as it's used just as end token
         * mark here. So, we can use this category.
         */
        END_LOOK_AHEAD,
        
        /** Defines token end. */
        END,

        /** Defines complete token. */
        COMPLETE
    }

}
