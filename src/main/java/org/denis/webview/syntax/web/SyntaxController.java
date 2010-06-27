package org.denis.webview.syntax.web;

import org.denis.webview.view.CommonViewHelper;
import org.denis.webview.view.ViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Zhdanov
 * @since 21.06.2010
 */
@Controller
public class SyntaxController {

    private static final String HIGHLIGHT_VIEW_NAME = "syntax";

    private final CommonViewHelper viewHelper;

    @Autowired
    public SyntaxController(CommonViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    @RequestMapping("/syntax/**")
//    public ModelAndView handle(@RequestParam(IDE_PARAMETER_NAME) String ide) {
    public ModelAndView handle(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
//        for (Map.Entry<String, MultipartFile> entry : request.getFileMap().entrySet()) {
//            System.out.println(entry.getKey());
//        }
        Map<String, String> parameters = new HashMap<String, String>();
//        parameters.put(IDE_PARAMETER_NAME, ide);
        return viewHelper.map(HIGHLIGHT_VIEW_NAME, ViewType.SYNTAX, parameters);
    }
}
