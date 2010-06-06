package org.denis.webview.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

/**
 * //TODO den add doc
 *
 * @author Denis Zhdanov
 * @since Jun 5, 2010
 */
@Controller
public class TargetContentController {

    private final StaticContentViewHelper viewHelper;

    @Autowired
    public TargetContentController(StaticContentViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    @RequestMapping("/{view}.*")
    public ModelAndView handle(@PathVariable("view") String view) throws MalformedURLException {
        return viewHelper.map(view);
    }
}
