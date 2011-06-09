package org.denis.webview.syntax.output.markup.inline.netbeans;

import org.denis.webview.config.Profile;
import org.denis.webview.syntax.logic.java.JavaTokenType;
import org.denis.webview.syntax.output.markup.inline.StyleAttribute;
import org.denis.webview.syntax.output.markup.inline.StyleRule;
import org.denis.webview.syntax.output.markup.inline.StyleSchemeImpl;
import org.springframework.stereotype.Component;

/**
 * @author Denis Zhdanov
 * @since 6/9/11 7:08 PM
 */
@Component
public class NetbeansJavaStyleScheme extends StyleSchemeImpl {

    public NetbeansJavaStyleScheme() {
        super(Profile.NETBEANS);

        registerComments();
        registerLiterals();
        registerOthers();
    }

    private void registerComments() {
        StyleRule color = new StyleRule(StyleAttribute.COLOR, "#969696");
        for (JavaTokenType tokenType : JavaTokenType.COMMENTS) {
            register(tokenType, color);
        }
        
        register(JavaTokenType.JAVADOC_TAG_START, StyleRule.BOLD);
        register(JavaTokenType.JAVADOC_HTML_TAG_START, StyleRule.BOLD);
    }

    private void registerLiterals() {
        StyleRule color = new StyleRule(StyleAttribute.COLOR, "#ce7b00");
        register(JavaTokenType.STRING_LITERAL_START, color);
        register(JavaTokenType.CHAR_LITERAL, color);
    }

    private void registerOthers() {
        register(JavaTokenType.KEYWORD, new StyleRule(StyleAttribute.COLOR, "#0000ee"));
    }
}
