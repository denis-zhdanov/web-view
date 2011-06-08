package org.denis.webview.syntax.output.markup.inline;

/**
 * Enumerates available style attributes.
 * 
 * @author Denis Zhdanov
 * @since 6/7/11 6:30 PM
 */
public enum StyleAttribute {
    COLOR, BACKGROUND_COLOR,
    
    FONT_WEIGHT, FONT_STYLE, TEXT_DECORATION;

    private String cssName;

    StyleAttribute() {
        cssName = toString().toLowerCase().replace("_", "-");
    }

    public String getCssName() {
        return cssName;
    }
}
