package org.denis.webview.syntax.output.markup.inline;

/**
 * Holds style attribute and its value.
 * 
 * @author Denis Zhdanov
 * @since 6/7/11 6:48 PM
 */
public class StyleRule {
    
    public static final StyleRule ITALIC = new StyleRule(StyleAttribute.FONT_STYLE, "italic");
    public static final StyleRule BOLD = new StyleRule(StyleAttribute.FONT_WEIGHT, "bold");
    public static final StyleRule UNDERLINE = new StyleRule(StyleAttribute.TEXT_DECORATION, "underline");

    private final StyleAttribute attribute;
    private final String         value;

    public StyleRule(StyleAttribute attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public StyleAttribute getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }
}
