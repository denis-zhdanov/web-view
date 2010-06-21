package org.denis.webview.staticcontent;

import org.denis.webview.view.CommonViewHelper;
import org.denis.webview.view.ViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Denis Zhdanov
 * @since 21.06.2010
 */
@Component
public class StaticViewHelper {

    private final AtomicReference<ServletContext> servletContext = new AtomicReference<ServletContext>();
    private final AtomicReference<String> internalContentPattern = new AtomicReference<String>();
    private final AtomicReference<String> defaultViewName = new AtomicReference<String>();
    private final AtomicReference<CommonViewHelper> commonHelper = new AtomicReference<CommonViewHelper>();

    public ModelAndView map(String viewName) throws MalformedURLException {
        String internalContentPath = String.format(internalContentPattern.get(), viewName);
        String viewToUse = viewName;
        if (servletContext.get().getResource(internalContentPath) == null) {
            viewToUse = defaultViewName.get();
        }

        return commonHelper.get().map(viewToUse, ViewType.STATIC);
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

    @Autowired
    public void setServletContext(ServletContext context) {
        servletContext.set(context);
    }

    @Autowired
    public void setCommonHelper(CommonViewHelper helper) {
        commonHelper.set(helper);
    }
}
