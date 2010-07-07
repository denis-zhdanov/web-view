package org.denis.webview.util.io;

import org.junit.Test;

/**
 * @author Denis Zhdanov
 * @since 07/04/2010
 */
public class HtmlEntityDecodingReaderTest extends AbstractReplacingFilterReaderTest<HtmlEntityDecodingReader> {

    public HtmlEntityDecodingReaderTest() {
        super(HtmlEntityDecodingReader.class);
    }

    @Test(expected = IllegalStateException.class)
    public void emptyDecimalEntity() throws Exception {
        getReader("&#;").read(new char[8]);
    }
    
    @Test(expected = IllegalStateException.class)
    public void tooManySymbolsAtDecimalEntity() throws Exception {
        getReader(String.format("&#%s1;", Integer.toString(Character.MAX_VALUE))).read(new char[16]);
    }

    @Test
    public void maxDecimalSymbols() throws Exception {
        doTest("&#1090;&#1077;&#1089;&#1090;", "\u0442\u0435\u0441\u0442");
    }

    @Test
    public void lessThanMaxDecimalSymbols() throws Exception {
        doTest("&#33;&#36;&#35;", "!$#");
    }

    //TODO den add tests for hex
}
