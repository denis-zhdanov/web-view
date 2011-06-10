package org.denis.webview.settings;

import org.apache.log4j.Logger;
import org.denis.webview.config.MarkupType;
import org.denis.webview.config.Profile;
import org.denis.webview.config.SourceType;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application-level representation of the settings to use.
 * <p/>
 * Not thread-safe.
 * 
 * @author Denis Zhdanov
 * @since 6/10/11 10:27 AM
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST,  proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Settings {

    private static final Logger LOG = Logger.getLogger(Settings.class);
    
    private static final Map<String, Processor<?>> PROCESSORS_BY_PARAM_NAME = new HashMap<String, Processor<?>>();

    private static final ProfileProcessor    PROFILE_PROCESSOR     = new ProfileProcessor();
    private static final SourceTypeProcessor SOURCE_TYPE_PROCESSOR = new SourceTypeProcessor();
    private static final MarkupTypeProcessor MARKUP_TYPE_PROCESSOR = new MarkupTypeProcessor();
    
    static {
        final Field[] fields = Settings.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if ((field.getModifiers() & Modifier.STATIC) != 0 && Processor.class.isAssignableFrom(field.getType())) {
                try {
                    final Processor<?> processor = (Processor) field.get(null);
                    PROCESSORS_BY_PARAM_NAME.put(processor.getParamName(), processor);
                } catch (Exception e) {
                    LOG.error("Unexpected exception during setting up highlight settings infrastructure", e);
                }
            }
        }
    }

    private static final String CURRENT_SETTINGS_PARAM_NAME = "current";
    private static final String ALL_SETTINGS_PARAM_NAME     = "all";
    
    private final Map<String, Object> rendererSettings = new HashMap<String, Object>();

    private Profile    profile;
    private SourceType sourceType;
    private MarkupType markupType;

    public Settings() {
        // Init defaults.
        setProfile(Profile.IDEA);
        setSourceType(SourceType.JAVA);
        setMarkupType(MarkupType.INLINE);
        for (Processor<?> processor : PROCESSORS_BY_PARAM_NAME.values()) {
            processor.init(this);
        }
    }

    /**
     * Registers target setting within the current object.
     * 
     * @param key      target key
     * @param value    target value
     */
    public void setSetting(String key, String value) {
        final Processor<?> processor = PROCESSORS_BY_PARAM_NAME.get(key);
        if (processor != null) {
            processor.update(value, this);
        }
    }
    
    /**
     * @return      settings to deliver to the rendering context for the actual rendering
     */
    public Map<String, Object> getRendererSettings() {
        return rendererSettings;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        PROFILE_PROCESSOR.update(profile, this);
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        SOURCE_TYPE_PROCESSOR.update(sourceType, this);
    }

    public MarkupType getMarkupType() {
        return markupType;
    }

    public void setMarkupType(MarkupType markupType) {
        MARKUP_TYPE_PROCESSOR.update(markupType, this);
    }

    //TODO den add doc
    private static abstract class Processor<T> {

        private final Map<T, String> VALUE_TO_STRING   = new HashMap<T, String>();
        private final Map<String, T> STRING_TO_VALUE   = new HashMap<String, T>();
        private final List<String>   ALL_STRING_VALUES = new ArrayList<String>();
        
        private final String paramName;
        
        Processor(String paramName, T... values) {
            this.paramName = paramName;
            for (T value : values) {
                String s = value.toString().toLowerCase().replace('_', '-');
                VALUE_TO_STRING.put(value, s);
                STRING_TO_VALUE.put(s, value);
                ALL_STRING_VALUES.add(s);
            }
        }

        @SuppressWarnings("unchecked")
        public void init(Settings settings) {
            Map<String, List<String>> map 
                    = (Map<String, List<String>>) settings.rendererSettings.get(ALL_SETTINGS_PARAM_NAME);
            if (map == null) {
                settings.rendererSettings.put(ALL_SETTINGS_PARAM_NAME, map = new HashMap<String, List<String>>());
            }
            map.put(paramName, ALL_STRING_VALUES);
        }
        
        public void update(String value, Settings settings) {
            final T t = STRING_TO_VALUE.get(value);
            if (t != null) {
                doUpdate(t, settings);
            } 
        }

        @SuppressWarnings("unchecked")
        public void update(T value, Settings settings) {
            doUpdate(value, settings);
            
            final String s = VALUE_TO_STRING.get(value);
            if (s == null) {
                return;
            }
            Map<String, String> map = (Map<String, String>) settings.rendererSettings.get(CURRENT_SETTINGS_PARAM_NAME);
            if (map == null) {
                settings.rendererSettings.put(CURRENT_SETTINGS_PARAM_NAME, map = new HashMap<String, String>());
            } 
            map.put(paramName, s);
        }

        public String getParamName() {
            return paramName;
        }

        protected abstract void doUpdate(T value, Settings settings);
    }
    
    private static class ProfileProcessor extends Processor<Profile> {

        ProfileProcessor() {
            super("profile", Profile.values());
        }

        @Override
        protected void doUpdate(Profile value, Settings settings) {
            settings.profile = value;
        }
    }
    
    private static class SourceTypeProcessor extends Processor<SourceType> {
        
        SourceTypeProcessor() {
            super("language", SourceType.values());
        }

        @Override
        protected void doUpdate(SourceType value, Settings settings) {
            settings.sourceType = value;
        }
    }

    private static class MarkupTypeProcessor extends Processor<MarkupType> {

        MarkupTypeProcessor() {
            super("markup", MarkupType.values());
        }

        @Override
        public void update(MarkupType value, Settings settings) {
            //TODO den remove this method as soon as markup type setting is exposed to the end user.
            settings.markupType = value;
        }

        @Override
        protected void doUpdate(MarkupType value, Settings settings) {
            settings.markupType = value;
        }
    }
}
