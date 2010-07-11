package org.denis.webview.staticcontent;

import org.denis.webview.view.CommonViewHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.MalformedURLException;

/**
 * //TODO den add doc
 *
 * @author Denis Zhdanov
 * @since Jun 5, 2010
 */
@Controller
public class TargetContentController {

    private final StaticViewHelper viewHelper;

    @Autowired
    public TargetContentController(StaticViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    @RequestMapping("/{view}.*")
    public ModelAndView handle(@PathVariable("view") String view) throws MalformedURLException {
        return viewHelper.map(view);
    }
}