package org.denis.webview.syntax;

import org.denis.webview.view.CommonViewHelper;
import org.denis.webview.view.ViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Zhdanov
 * @since 21.06.2010
 */
@Controller
public class SyntaxController {

    private static final String HIGHLIGHT_VIEW_NAME = "highlight";
    private static final String IDE_PARAMETER_NAME = "ide";

    private final CommonViewHelper viewHelper;

    @Autowired
    public SyntaxController(CommonViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    @RequestMapping("/syntax/**")
    public ModelAndView handle(@RequestParam(IDE_PARAMETER_NAME) String ide) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(IDE_PARAMETER_NAME, ide);
        return viewHelper.map(HIGHLIGHT_VIEW_NAME, ViewType.SYNTAX, parameters);
    }
}
