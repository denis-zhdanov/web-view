package org.denis.webview.syntax.output.markup;

import org.denis.webview.config.MarkupType;
import org.denis.webview.config.Profile;
import org.denis.webview.settings.Settings;
import org.denis.webview.syntax.logic.TokenType;
import org.denis.webview.syntax.output.markup.inline.InlineStyleMarkupScheme;
import org.denis.webview.syntax.output.markup.inline.StyleScheme;
import org.denis.webview.syntax.output.markup.inline.StyleSchemeImpl;
import org.denis.webview.util.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Denis Zhdanov
 * @since 6/7/11 8:03 PM
 */
@Component
public class MarkupSchemeProvider {

    /**
     * Holds registered schemes by keys like (markup type; profile).
     */
    private final Map<Tuple, MarkupScheme> schemes = new ConcurrentHashMap<Tuple, MarkupScheme>();

    private Settings settings;
    
    /**
     * Allows to retrieve markup scheme to use.
     *
     * @return  markup scheme for the current settings if any is registered; <code>null</code> otherwise
     */
    public MarkupScheme getScheme() {
        return schemes.get(new Tuple(settings.getMarkupType(), settings.getProfile()));
    }

    @Autowired
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Autowired
    public void initInlineMarkup(Collection<StyleScheme> preRegisteredSchemes) {
        Map<Profile, StyleSchemeImpl> map = new HashMap<Profile, StyleSchemeImpl>();
        for (StyleScheme preRegisteredScheme : preRegisteredSchemes) {
            StyleSchemeImpl scheme = map.get(preRegisteredScheme.getProfile());
            if (scheme == null) {
                map.put(preRegisteredScheme.getProfile(), scheme = new StyleSchemeImpl(preRegisteredScheme.getProfile()));
            }
            for (TokenType tokenType : preRegisteredScheme.getSupportedTokenTypes()) {
                scheme.register(tokenType, preRegisteredScheme.getRules(tokenType));
            }
        }
        for (Map.Entry<Profile, StyleSchemeImpl> entry : map.entrySet()) {
            schemes.put(new Tuple(MarkupType.INLINE, entry.getKey()), new InlineStyleMarkupScheme(entry.getValue()));
        }
    }
}
