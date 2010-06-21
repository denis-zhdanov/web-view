package org.denis.webview.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

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

    private final ConcurrentMap<ViewType, String> viewTypesTemplates = new ConcurrentHashMap<ViewType, String>();
    private final AtomicReference<String> baseTemplateName = new AtomicReference<String>();
    private final AtomicReference<String> internalContentVarName = new AtomicReference<String>();

    public ModelAndView map(String viewName, ViewType viewType) {
        return map(viewName, viewType, Collections.<String, String>emptyMap());
    }

    //TODO den add doc
    public ModelAndView map(String viewName, ViewType viewType, Map<String, ?> parameters) {
        String contentPath = String.format(viewTypesTemplates.get(viewType), viewName);
        Map<String, Object> parametersToUse = new HashMap<String, Object>(parameters);
        parametersToUse.put(internalContentVarName.get(), contentPath);
        return new ModelAndView(baseTemplateName.get(), parametersToUse);
    }

    @Resource(name = "vewTypesTemplates")
    public void setViewTypeTemplates(Map<ViewType, String> templates) {
        viewTypesTemplates.putAll(templates);
    }

    //TODO den add doc
    @Value("#{webContent.baseTemplateName}")
    public void setBaseTemplateName(String name) {
        baseTemplateName.set(name);
    }    

    //TODO den add doc
    @Value("#{webContent.internalContentVarName}")
    public void setInternalContentVarName(String name) {
        internalContentVarName.set(name);
    }
}
