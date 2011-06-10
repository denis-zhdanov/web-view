package org.denis.webview.view;

import org.denis.webview.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility class that encapsulates the logic of mapping view name from incoming request to
 * Spring MVC view to use.
 * <p/>
 * Thread-safe.
 *
 * @author Denis Zhdanov
 * @since Jun 5, 2010
 */
@Component
public class CommonViewHelper {

    /** Name of the basic template name to use, i.e. the main 'skeleton' page data. */
    private static final String BASE_TEMPLATE_NAME = "common-template";
    /** Name of the rendering context variable that holds settings info. */
    private static final String SETTINGS_VAR_NAME = "settings";
    //TODO den add doc
    private static final String INTERNAL_CONTENT_VAR_NAME = "content";
    
    private final ConcurrentMap<ViewType, String> viewTypesTemplates = new ConcurrentHashMap<ViewType, String>();

    private Settings settings;
    
    public ModelAndView map(String viewName, ViewType viewType) {
        return map(viewName, viewType, Collections.<String, String>emptyMap());
    }

    //TODO den add doc
    public ModelAndView map(String viewName, ViewType viewType, Map<String, ?> parameters) {
        String contentPath = String.format(viewTypesTemplates.get(viewType), viewName);
        Map<String, Object> parametersToUse = new HashMap<String, Object>(parameters);
        parametersToUse.put(INTERNAL_CONTENT_VAR_NAME, contentPath);
        parametersToUse.put(SETTINGS_VAR_NAME, settings.getRendererSettings());
        return new ModelAndView(BASE_TEMPLATE_NAME, parametersToUse);
    }

    @Resource(name = "vewTypesTemplates")
    public void setViewTypeTemplates(Map<ViewType, String> templates) {
        viewTypesTemplates.putAll(templates);
    }

    @Autowired
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
