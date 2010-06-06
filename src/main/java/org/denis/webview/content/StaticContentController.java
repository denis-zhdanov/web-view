package org.denis.webview.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * //TODO den add doc
 *
 * @author Denis Zhdanov
 * @since Jun 4, 2010
 */
@Controller
public class StaticContentController {

    private final AtomicReference<String> defaultViewName = new AtomicReference<String>();
    private final StaticContentViewHelper viewHelper;

    @Autowired
    public StaticContentController(StaticContentViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    @RequestMapping("/**")
    public ModelAndView handle() throws MalformedURLException {        
        return viewHelper.map(defaultViewName.get());
    }

    @Value("#{webContent.defaultViewName}")
    public void setDefaultViewName(String name) {
        defaultViewName.set(name);
    }
}
