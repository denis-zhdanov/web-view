package org.denis.webview.syntax.output.markup.inline.idea;

import org.denis.webview.config.Profile;
import org.denis.webview.syntax.logic.java.JavaTokenType;
import org.denis.webview.syntax.output.markup.inline.*;
import org.denis.webview.syntax.output.markup.inline.StyleAttribute;
import org.denis.webview.syntax.output.markup.inline.StyleRule;
import org.springframework.stereotype.Component;

/**
 * @author Denis Zhdanov
 * @since 6/7/11 6:31 PM
 */
@Component
public class IdeaJavaStyleScheme extends StyleSchemeImpl {

    public IdeaJavaStyleScheme() {
        super(Profile.IDEA);

        // Comments.
        register(JavaTokenType.SINGLE_LINE_COMMENT_START, new StyleRule(StyleAttribute.COLOR, "#808080"));
        register(JavaTokenType.SINGLE_LINE_COMMENT_START, StyleRule.ITALIC);
    }
}
