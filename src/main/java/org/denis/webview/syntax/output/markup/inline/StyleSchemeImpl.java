package org.denis.webview.syntax.output.markup.inline;

import org.denis.webview.config.Profile;
import org.denis.webview.syntax.logic.TokenType;

import java.util.*;

/**
 * @author Denis Zhdanov
 * @since 6/7/11 6:36 PM
 */
public class StyleSchemeImpl implements StyleScheme {

    private final Map<TokenType, Collection<StyleRule>> rules
            = new HashMap<TokenType, Collection<StyleRule>>();

    private final Profile profile;

    public StyleSchemeImpl(Profile profile) {
        this.profile = profile;
    }

    public Set<TokenType> getSupportedTokenTypes() {
        return rules.keySet();
    }

    @Override
    public Profile getProfile() {
        return profile;
    }

    @Override
    public Collection<StyleRule> getRules(TokenType tokenType) {
        final Collection<StyleRule> result = rules.get(tokenType);
        return result == null ? Collections.<StyleRule>emptyList() : result;
    }

    public void register(TokenType tokenType, StyleRule ... rules) {
        register(tokenType, Arrays.asList(rules));
    }

    public void register(TokenType tokenType, Collection<StyleRule> rulesToRegister) {
        Collection<StyleRule> tokenRules = rules.get(tokenType);
        if (tokenRules == null) {
            rules.put(tokenType, tokenRules = new ArrayList<StyleRule>());
        }
        tokenRules.addAll(rulesToRegister);
    }
}
