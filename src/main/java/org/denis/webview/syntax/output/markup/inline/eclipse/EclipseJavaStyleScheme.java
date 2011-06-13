package org.denis.webview.syntax.output.markup.inline.eclipse;

import org.denis.webview.config.Profile;
import org.denis.webview.syntax.logic.java.JavaTokenType;
import org.denis.webview.syntax.output.markup.inline.StyleAttribute;
import org.denis.webview.syntax.output.markup.inline.StyleRule;
import org.denis.webview.syntax.output.markup.inline.StyleSchemeImpl;
import org.springframework.stereotype.Component;

/**
 * @author Denis Zhdanov
 * @since 6/9/11 6:07 PM
 */
@Component
public class EclipseJavaStyleScheme extends StyleSchemeImpl {

    public EclipseJavaStyleScheme() {
        super(Profile.ECLIPSE);

        registerComments();
        registerLiterals();
        registerOthers();
    }

    private void registerComments() {
        final StyleRule commentColor = new StyleRule(StyleAttribute.COLOR, "#3f7f5f");
        register(JavaTokenType.SINGLE_LINE_COMMENT_START, commentColor);
        register(JavaTokenType.MULTI_LINE_COMMENT_START, commentColor);

        register(JavaTokenType.JAVADOC_START, new StyleRule(StyleAttribute.COLOR, "#3f5fbf"));
        register(JavaTokenType.JAVADOC_HTML_TAG_START, new StyleRule(StyleAttribute.COLOR, "#837f9f"));
        register(JavaTokenType.JAVADOC_TAG_START, new StyleRule(StyleAttribute.COLOR, "#7f9fc5"));
    }

    private void registerLiterals() {
        final StyleRule rule = new StyleRule(StyleAttribute.COLOR, "#3f7f5f");
        register(JavaTokenType.STRING_LITERAL_START, rule);
        register(JavaTokenType.CHAR_LITERAL, rule);
    }

    private void registerOthers() {
        register(JavaTokenType.KEYWORD, new StyleRule(StyleAttribute.COLOR, "#7f0058"), StyleRule.BOLD);
        register(JavaTokenType.ANNOTATION_START, new StyleRule(StyleAttribute.COLOR, "#646464"));
    }
}
