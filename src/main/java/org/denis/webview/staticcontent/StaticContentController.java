package org.denis.webview.staticcontent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.MalformedURLException;

/**
 * Controller that manages 'static content', i.e. all application tabs at our case.
 *
 * @author Denis Zhdanov
 * @since Jun 5, 2010
 */
@Controller
public class StaticContentController {

    private final StaticViewHelper viewHelper;

    @Autowired
    public StaticContentController(StaticViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    @RequestMapping("/**/*")
    public ModelAndView handle() throws MalformedURLException {
        return viewHelper.map(StaticViewHelper.DEFAULT_VIEW_NAME);
    }

    @RequestMapping("/{view}.*")
    public ModelAndView handle(@PathVariable("view") String view) throws MalformedURLException {
        return viewHelper.map(view);
    }
}
