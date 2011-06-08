package org.denis.webview.syntax.output.markup.inline;

/**
 * Enumerates available style attributes.
 * 
 * @author Denis Zhdanov
 * @since 6/7/11 6:30 PM
 */
public enum StyleAttribute {
    //TODO den implement rewrite without such explicit definition
    COLOR("color"), FONT_WEIGHT("font-weight"), FONT_STYLE("font-style"), TEXT_DECORATION("text-decoration");

    private final String cssName;

    StyleAttribute(String cssName) {
        this.cssName = cssName;
    }

    public String getCssName() {
        return cssName;
    }
}
