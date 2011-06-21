package org.denis.webview.staticcontent;

import org.denis.webview.view.CommonViewHelper;
import org.denis.webview.view.ViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Denis Zhdanov
 * @since 21.06.2010
 */
@Component
public class StaticViewHelper {

    /** Default tab to go. */
    public static final String DEFAULT_VIEW_NAME = "go";

    /** Content entry path pattern. */
    private static final String CONTENT_PATH_PATTERN = "/WEB-INF/vm/content/%s.vm";

    private final AtomicReference<ServletContext> servletContext = new AtomicReference<ServletContext>();
    private final AtomicReference<CommonViewHelper> commonHelper = new AtomicReference<CommonViewHelper>();

    public ModelAndView map(String viewName) throws MalformedURLException {
        String internalContentPath = String.format(CONTENT_PATH_PATTERN, viewName);
        String viewToUse = viewName;
        if (servletContext.get().getResource(internalContentPath) == null) {
            viewToUse = DEFAULT_VIEW_NAME;
        }

        return commonHelper.get().map(viewToUse, ViewType.STATIC);
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
