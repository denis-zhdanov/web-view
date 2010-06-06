package org.denis.webview.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.Collections;
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
public class StaticContentViewHelper {

    private final AtomicReference<ServletContext> servletContext = new AtomicReference<ServletContext>();
    private final AtomicReference<String> internalContentPattern = new AtomicReference<String>();
    private final AtomicReference<String> defaultViewName = new AtomicReference<String>();
    private final AtomicReference<String> baseTemplateName = new AtomicReference<String>();
    private final AtomicReference<String> internalContentVarName = new AtomicReference<String>();

    public ModelAndView map(String viewName) throws MalformedURLException {
        String internalContentPath = mapToInternalContentPath(viewName);
        String viewToUse = viewName;
        if (servletContext.get().getResource(internalContentPath) == null) {
            viewToUse = defaultViewName.get();
        }
        return new ModelAndView(
                baseTemplateName.get(), 
                Collections.singletonMap(internalContentVarName.get(), viewToUse)
        );
    }

    //TODO den add doc
    @Value("#{webContent.defaultViewName}")
    public void setDefaultViewName(String name) {
        defaultViewName.set(name);
    }

    //TODO den add doc
    @Value("#{webContent.internalContentPattern}")
    public void setInternalContentPattern(String pattern) {
        internalContentPattern.set(pattern);
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

    @Autowired
    public void setServletContext(ServletContext context) {
        servletContext.set(context);
    }

    //TODO den add doc
    private String mapToInternalContentPath(String internalContentName) {
        return String.format(internalContentPattern.get(), internalContentName);
    }
}
