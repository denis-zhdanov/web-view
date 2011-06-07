package org.denis.webview.syntax.output.markup.inline;

import org.denis.webview.config.Profile;
import org.denis.webview.syntax.logic.TokenType;

import java.util.Collection;
import java.util.Set;

/**
 * Defines common contract for particular rule-based style settings profile.
 * 
 * @author Denis Zhdanov
 * @since 6/7/11 6:32 PM
 */
public interface StyleScheme {

    Set<TokenType> getSupportedTokenTypes();

    Profile getProfile();

    /**
     * Allows to ask for style rules to use for the target token type.
     * 
     * @param tokenType    target token type
     * @return             style rules for the given token type (if any); empty collection otherwise
     */
    Collection<StyleRule> getRules(TokenType tokenType);
}
