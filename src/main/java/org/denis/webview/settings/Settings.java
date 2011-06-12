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
    
    private final Map<String, Map<String, Object>> rendererSettings = new HashMap<String, Map<String, Object>>();

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
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    public void setSetting(String key, String value) {
        final Processor<?> processor = PROCESSORS_BY_PARAM_NAME.get(key);
        if (processor != null) {
            processor.update(value, this);
        }
    }
    
    /**
     * @return      settings to deliver to the rendering context for the actual rendering
     */
    public Map<String, Map<String, Object>> getRendererSettings() {
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

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private static <T> Map<String, T> getRendererSettings(Map<String, Map<String, Object>> map, String type) {
        Map<String, Object> result = map.get(type);
        if (result == null) {
            map.put(type, result = new HashMap<String, Object>());
        }
        return (Map<String, T>)result;
    }

    /**
     * Base class for the settings property processors.
     *
     * @param <T>   target property type
     */
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

        @SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
        public void init(Settings settings) {
            Map<String, List<String>> map = getRendererSettings(settings.rendererSettings, ALL_SETTINGS_PARAM_NAME);
            map.put(paramName, ALL_STRING_VALUES);
        }
        
        @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
        public void update(String value, Settings settings) {
            final T t = STRING_TO_VALUE.get(value);
            if (t != null) {
                doUpdate(t, settings);
                Map<String, Object> current = getRendererSettings(settings.rendererSettings, CURRENT_SETTINGS_PARAM_NAME);
                current.put(paramName, value);
            }
        }

        @SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
        public void update(T value, Settings settings) {
            doUpdate(value, settings);
            
            final String s = VALUE_TO_STRING.get(value);
            if (s == null) {
                return;
            }
            Map<String, String> map = getRendererSettings(settings.rendererSettings, CURRENT_SETTINGS_PARAM_NAME);
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
