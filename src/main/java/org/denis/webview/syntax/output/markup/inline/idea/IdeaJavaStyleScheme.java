package org.denis.webview.syntax.output.markup.inline.idea;

import org.denis.webview.config.Profile;
import org.denis.webview.syntax.logic.java.JavaTokenType;
import org.denis.webview.syntax.output.markup.inline.StyleAttribute;
import org.denis.webview.syntax.output.markup.inline.StyleRule;
import org.denis.webview.syntax.output.markup.inline.StyleSchemeImpl;
import org.springframework.stereotype.Component;

/**
 * @author Denis Zhdanov
 * @since 6/7/11 6:31 PM
 */
@Component
public class IdeaJavaStyleScheme extends StyleSchemeImpl {

    public IdeaJavaStyleScheme() {
        super(Profile.IDEA);
        
        registerComments();
    }

    private void registerComments() {
        StyleRule[] commonRules = {new StyleRule(StyleAttribute.COLOR, "#808080"), StyleRule.ITALIC};
        for (JavaTokenType commentType : JavaTokenType.COMMENTS) {
            register(commentType, commonRules);
        }
        
        register(JavaTokenType.JAVADOC_TAG_START, StyleRule.UNDERLINE, StyleRule.BOLD);
    }
}
