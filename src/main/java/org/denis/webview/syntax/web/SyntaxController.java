package org.denis.webview.syntax.web;

import org.denis.webview.view.CommonViewHelper;
import org.denis.webview.view.ViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for syntax requests, i.e. requests to actually highlight target text.
 *
 * @author Denis Zhdanov
 * @since 21.06.2010
 */
@Controller
public class SyntaxController {

    private static final String HIGHLIGHT_VIEW_NAME = "syntax";
    private static final String HIGHLIGHTED_VAR_NAME = "highlighted";
    private static final String CURRENT_SETTINGS_VAR_NAME = "currentSettings";

    private final CommonViewHelper viewHelper;
    private SyntaxHighlightRenderable renderable;

    @Autowired
    public SyntaxController(CommonViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    @RequestMapping("/syntax/**")
    public ModelAndView handle(Reader reader) throws IOException {
        Map<String, Object> settings = new HashMap<String, Object>();
        renderable.prepare(reader, settings);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(HIGHLIGHTED_VAR_NAME, renderable);
        parameters.put(CURRENT_SETTINGS_VAR_NAME, settings);
        return viewHelper.map(HIGHLIGHT_VIEW_NAME, ViewType.SYNTAX, parameters);
    }

    @Autowired
    public void setRenderable(SyntaxHighlightRenderable renderable) {
        this.renderable = renderable;
    }
}
