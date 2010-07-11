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

    @Test(expected = IllegalStateException.class)
    public void incompleteDecimalEntity() throws Exception {
        HtmlEntityDecodingReader reader = getReader("&#123");
        reader.read(new char[16]);
        reader.read(new char[16]); // Read one more time in order to get EOF.
    }

    @Test
    public void maxDecimalSymbols() throws Exception {
        doTest("&#1090;&#1077;&#1089;&#1090;", "\u0442\u0435\u0441\u0442");
    }

    @Test
    public void lessThanMaxDecimalSymbols() throws Exception {
        doTest("&#33;&#36;&#35;", "!$#");
    }

    @Test(expected = IllegalStateException.class)
    public void tooManySymbolsAtHexEntity() throws Exception {
        getReader(String.format("&#x%s1;", Integer.toBinaryString(Character.MAX_VALUE) + '1')).read(new char[16]);
    }

    @Test(expected = IllegalStateException.class)
    public void incompleteHexEntity() throws Exception {
        HtmlEntityDecodingReader reader = getReader("&#x123");
        reader.read(new char[16]);
        reader.read(new char[16]); // Read one more time in order to get EOF.
    }
    
    @Test
    public void hexSymbols() throws Exception {
        doTest("&#x442;&#x0435;&#x441;&#x0442;", "\u0442\u0435\u0441\u0442");
    }

    @Test
    public void characterEntities() throws Exception {
        doTest("&amp;&quot;&lt;", "&\"<");
    }
    
    @Test(expected = IllegalStateException.class)
    public void invalidCharacterEntity() throws Exception {
        getReader("&ampp;").read(new char[16]);
    }

    @Test(expected = IllegalStateException.class)
    public void incompleteCharacterEntity() throws Exception {
        HtmlEntityDecodingReader reader = getReader("&amp");
        reader.read(new char[16]);
        reader.read(new char[16]); // Read one more time in order to get EOF.
    }
}
