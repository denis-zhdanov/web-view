package org.denis.webview.util.io;

import org.denis.webview.util.string.CharArrayCharSequence;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Symbol stream that provides transparent decoding of
 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">HTML character entities</a> and
 * <a href="http://www.w3.org/International/tutorials/tutorial-char-enc/#Slide0430">numeric character references</a>
 * from underlying stream.
 *
 * @author Denis Zhdanov
 * @since Jun 27, 2010
 */
public class HtmlEntityDecodingReader extends AbstractReplacingFilterReader {

    private static final char ENTITY_END_SIGN = ';';
    
    private static final Map<CharSequence, Character> ENTITIES = new HashMap<CharSequence, Character>();
    static {
        // ISO-8859-1 characters
        registerEntity("nbsp", 160);
        registerEntity("iexcl", 161);
        registerEntity("cent", 162);
        registerEntity("pound", 163);
        registerEntity("curren", 164);
        registerEntity("yen", 165);
        registerEntity("brvbar", 166);
        registerEntity("sect", 167);
        registerEntity("uml", 168);
        registerEntity("copy", 169);
        registerEntity("ordf", 170);
        registerEntity("laquo", 171);
        registerEntity("not", 172);
        registerEntity("shy", 173);
        registerEntity("reg", 174);
        registerEntity("macr", 175);
        registerEntity("deg", 176);
        registerEntity("plusmn", 177);
        registerEntity("sup2", 178);
        registerEntity("sup3", 179);
        registerEntity("acute", 180);
        registerEntity("micro", 181);
        registerEntity("para", 182);
        registerEntity("middot", 183);
        registerEntity("cedil", 184);
        registerEntity("sup1", 185);
        registerEntity("ordm", 186);
        registerEntity("raquo", 187);
        registerEntity("frac14", 188);
        registerEntity("frac12", 189);
        registerEntity("frac34", 190);
        registerEntity("iquest", 191);
        registerEntity("Agrave", 192);
        registerEntity("Aacute", 193);
        registerEntity("Acirc", 194);
        registerEntity("Atilde", 195);
        registerEntity("Auml", 196);
        registerEntity("Aring", 197);
        registerEntity("AElig", 198);
        registerEntity("Ccedil", 199);
        registerEntity("Egrave", 200);
        registerEntity("Eacute", 201);
        registerEntity("Ecirc", 202);
        registerEntity("Euml", 203);
        registerEntity("Igrave", 204);
        registerEntity("Iacute", 205);
        registerEntity("Icirc", 206);
        registerEntity("Iuml", 207);
        registerEntity("ETH", 208);
        registerEntity("Ntilde", 209);
        registerEntity("Ograve", 210);
        registerEntity("Oacute", 211);
        registerEntity("Ocirc", 212);
        registerEntity("Otilde", 213);
        registerEntity("Ouml", 214);
        registerEntity("times", 215);
        registerEntity("Oslash", 216);
        registerEntity("Ugrave", 217);
        registerEntity("Uacute", 218);
        registerEntity("Ucirc", 219);
        registerEntity("Uuml", 220);
        registerEntity("Yacute", 221);
        registerEntity("THORN", 222);
        registerEntity("szlig", 223);
        registerEntity("agrave", 224);
        registerEntity("aacute", 225);
        registerEntity("acirc", 226);
        registerEntity("atilde", 227);
        registerEntity("auml", 228);
        registerEntity("aring", 229);
        registerEntity("aelig", 230);
        registerEntity("ccedil", 231);
        registerEntity("egrave", 232);
        registerEntity("eacute", 233);
        registerEntity("ecirc", 234);
        registerEntity("euml", 235);
        registerEntity("igrave", 236);
        registerEntity("iacute", 237);
        registerEntity("icirc", 238);
        registerEntity("iuml", 239);
        registerEntity("eth", 240);
        registerEntity("ntilde", 241);
        registerEntity("ograve", 242);
        registerEntity("oacute", 243);
        registerEntity("ocirc", 244);
        registerEntity("otilde", 245);
        registerEntity("ouml", 246);
        registerEntity("divide", 247);
        registerEntity("oslash", 248);
        registerEntity("ugrave", 249);
        registerEntity("uacute", 250);
        registerEntity("ucirc", 251);
        registerEntity("uuml", 252);
        registerEntity("yacute", 253);
        registerEntity("thorn", 254);
        registerEntity("yuml", 255);

        // Mathematical, Greek and Symbolic characters
        registerEntity("fnof", 402);
        registerEntity("Alpha", 913);
        registerEntity("Beta", 914);
        registerEntity("Gamma", 915);
        registerEntity("Delta", 916);
        registerEntity("Epsilon", 917);
        registerEntity("Zeta", 918);
        registerEntity("Eta", 919);
        registerEntity("Theta", 920);
        registerEntity("Iota", 921);
        registerEntity("Kappa", 922);
        registerEntity("Lambda", 923);
        registerEntity("Mu", 924);
        registerEntity("Nu", 925);
        registerEntity("Xi", 926);
        registerEntity("Omicron", 927);
        registerEntity("Pi", 928);
        registerEntity("Rho", 929);
        registerEntity("Sigma", 931);
        registerEntity("Tau", 932);
        registerEntity("Upsilon", 933);
        registerEntity("Phi", 934);
        registerEntity("Chi", 935);
        registerEntity("Psi", 936);
        registerEntity("Omega", 937);
        registerEntity("alpha", 945);
        registerEntity("beta", 946);
        registerEntity("gamma", 947);
        registerEntity("delta", 948);
        registerEntity("epsilon", 949);
        registerEntity("zeta", 950);
        registerEntity("eta", 951);
        registerEntity("theta", 952);
        registerEntity("iota", 953);
        registerEntity("kappa", 954);
        registerEntity("lambda", 955);
        registerEntity("mu", 956);
        registerEntity("nu", 957);
        registerEntity("xi", 958);
        registerEntity("omicron", 959);
        registerEntity("pi", 960);
        registerEntity("rho", 961);
        registerEntity("sigmaf", 962);
        registerEntity("sigma", 963);
        registerEntity("tau", 964);
        registerEntity("upsilon", 965);
        registerEntity("phi", 966);
        registerEntity("chi", 967);
        registerEntity("psi", 968);
        registerEntity("omega", 969);
        registerEntity("thetasym", 977);
        registerEntity("upsih", 978);
        registerEntity("piv", 982);
        registerEntity("bull", 8226);
        registerEntity("hellip", 8230);
        registerEntity("prime", 8242);
        registerEntity("Prime", 8243);
        registerEntity("oline", 8254);
        registerEntity("frasl", 8260);
        registerEntity("weierp", 8472);
        registerEntity("image", 8465);
        registerEntity("real", 8476);
        registerEntity("trade", 8482);
        registerEntity("alefsym", 8501);
        registerEntity("larr", 8592);
        registerEntity("uarr", 8593);
        registerEntity("rarr", 8594);
        registerEntity("darr", 8595);
        registerEntity("harr", 8596);
        registerEntity("crarr", 8629);
        registerEntity("lArr", 8656);
        registerEntity("uArr", 8657);
        registerEntity("rArr", 8658);
        registerEntity("dArr", 8659);
        registerEntity("hArr", 8660);
        registerEntity("forall", 8704);
        registerEntity("part", 8706);
        registerEntity("exist", 8707);
        registerEntity("empty", 8709);
        registerEntity("nabla", 8711);
        registerEntity("isin", 8712);
        registerEntity("notin", 8713);
        registerEntity("ni", 8715);
        registerEntity("prod", 8719);
        registerEntity("sum", 8721);
        registerEntity("minus", 8722);
        registerEntity("lowast", 8727);
        registerEntity("radic", 8730);
        registerEntity("prop", 8733);
        registerEntity("infin", 8734);
        registerEntity("ang", 8736);
        registerEntity("and", 8743);
        registerEntity("or", 8744);
        registerEntity("cap", 8745);
        registerEntity("cup", 8746);
        registerEntity("int", 8747);
        registerEntity("there4", 8756);
        registerEntity("sim", 8764);
        registerEntity("cong", 8773);
        registerEntity("asymp", 8776);
        registerEntity("ne", 8800);
        registerEntity("equiv", 8801);
        registerEntity("le", 8804);
        registerEntity("ge", 8805);
        registerEntity("sub", 8834);
        registerEntity("sup", 8835);
        registerEntity("nsub", 8836);
        registerEntity("sube", 8838);
        registerEntity("supe", 8839);
        registerEntity("oplus", 8853);
        registerEntity("otimes", 8855);
        registerEntity("perp", 8869);
        registerEntity("sdot", 8901);
        registerEntity("lceil", 8968);
        registerEntity("rceil", 8969);
        registerEntity("lfloor", 8970);
        registerEntity("rfloor", 8971);
        registerEntity("lang", 9001);
        registerEntity("rang", 9002);
        registerEntity("loz", 9674);
        registerEntity("spades", 9824);
        registerEntity("clubs", 9827);
        registerEntity("hearts", 9829);
        registerEntity("diams", 9830);

        // Markup and internationalization.
        registerEntity("quot", 34);
        registerEntity("amp", 38);
        registerEntity("lt", 60);
        registerEntity("gt", 62);
        registerEntity("OElig", 338);
        registerEntity("oelig", 339);
        registerEntity("Scaron", 352);
        registerEntity("scaron", 353);
        registerEntity("Yml", 376);
        registerEntity("circ", 710);
        registerEntity("tilde", 732);
        registerEntity("ensp", 8194);
        registerEntity("emsp", 8195);
        registerEntity("thinsp", 8201);
        registerEntity("zwnj", 8204);
        registerEntity("zwj", 8205);
        registerEntity("lrm", 8206);
        registerEntity("rlm", 8207);
        registerEntity("ndash", 8211);
        registerEntity("mdash", 8212);
        registerEntity("lsquo", 8216);
        registerEntity("rsquo", 8217);
        registerEntity("sbquo", 8218);
        registerEntity("ldquo", 8220);
        registerEntity("rdquo", 8221);
        registerEntity("bdquo", 8222);
        registerEntity("dagger", 8224);
        registerEntity("Dagger", 8225);
        registerEntity("permil", 8240);
        registerEntity("lsaquo", 8249);
        registerEntity("rsaquo", 8250);
        registerEntity("euro", 8364);
    }
    private static void registerEntity(String name, int code) {
        ENTITIES.put(new CharArrayCharSequence(name), (char)code);
    }

    /**
     * Enumerates possible HTML entities encoding types.
     */
    private enum EntityEncodingType {

        /** The entity is encoded like '<code>&amp;#NNN;'</code> (where <code>'NNN'</code> are decimal numbers) */
        DECIMAL,

        /** The entity is encoded like '<code>&amp;#xHHH;'</code> (where <code>'HHH'</code> are hex numbers) */
        HEX,

        /** The entity is encoded via its unique name like '<code>&amp;NAME;'</code>, e.g. <code>'&amp;amp;'</code> */
        CHARACTER
    }

    private static final Map<EntityEncodingType, DecodingHelper> HELPERS
        = new HashMap<EntityEncodingType, DecodingHelper>();
    static {
        HELPERS.put(EntityEncodingType.DECIMAL, new DecimalDecodingHelper());
        HELPERS.put(EntityEncodingType.HEX, new HexDecodingHelper());
        HELPERS.put(EntityEncodingType.CHARACTER, new CharacterDecodingHelper());
    }

    private static final int MAX_REPLACEMENT_SIZE;
    static {
        int max = 0;
        for (DecodingHelper helper : HELPERS.values()) {
            max = Math.max(helper.getMaxSymbolsNumber(), max);
        }
        MAX_REPLACEMENT_SIZE = max;
    }

    public HtmlEntityDecodingReader(Reader in) {
        super(in, MAX_REPLACEMENT_SIZE);
    }

    @Override
    protected int copy(DataContext dataContext) throws IllegalStateException {
        int result = 0;
        for (
            int externalMaxOffset = dataContext.externalOffset + dataContext.externalLength;
            dataContext.internalStart < dataContext.internalEnd && dataContext.externalOffset < externalMaxOffset;
            ++result)
        {
            if (dataContext.internalBuffer[dataContext.internalStart] != '&') {
                dataContext.externalBuffer[dataContext.externalOffset++]
                    = dataContext.internalBuffer[dataContext.internalStart++];
                continue;
            }

            // Current internal buffer offset points to the entity start.
            if (!parseEntity(dataContext)) {
                break;
            }
        }
        return result;

    }

    /**
     * Assumes that given {@link DataContext#internalBuffer 'raw data' buffer} holds <code>'&amp;'</code> at
     * position identified by {@link DataContext#internalStart start offset} and tries to parse encoded entity.
     * <p/>
     * This method updates offsets stored at the given context only if the entity is decoded
     *
     * @param dataContext       target data holder
     * @return                  <code>true</code> if encoded entity is decoded; <code>false</code> otherwise
     * @throws IllegalStateException        if it's encountered that given <code>'raw'</code> data is invalid 
     */
    private static boolean parseEntity(DataContext dataContext) throws IllegalStateException {
        EntityEncodingType type;
        int startOffsetToUse = dataContext.internalStart;
        if (!offsetInBounds(dataContext, startOffsetToUse++)) {
            return false;
        }
        if (dataContext.internalBuffer[startOffsetToUse] == '#') {
            type = EntityEncodingType.DECIMAL;
            if (!offsetInBounds(dataContext, startOffsetToUse++)) {
                return false;
            }
            if (dataContext.internalBuffer[startOffsetToUse] == 'x') {
                startOffsetToUse++;
                type = EntityEncodingType.HEX;
            }
        } else {
            type = EntityEncodingType.CHARACTER;
        }

        DecodingHelper helper = HELPERS.get(type);
        if (helper == null) {
            throw new IllegalStateException(String.format("Can't decode HTML entity of type %s. Reason: no "
                + "decoding is setup for it. Configured only for the following entity types: %s",
                type, HELPERS.keySet()));
        }
        int code = 0;
        for (int i = startOffsetToUse; i < dataContext.internalEnd; ++i) {
            char c = dataContext.internalBuffer[i];
            if (c == ENTITY_END_SIGN) {
                if (i - startOffsetToUse <= 0) {
                    throw new IllegalStateException("Detected invalid HTML entity at the decorated symbol "
                        + "stream - '" + rawDataSeq(dataContext, i) + "'");
                }
                char decoded = helper.decode(dataContext, i, code);
                dataContext.externalBuffer[dataContext.externalOffset++] = decoded;
                dataContext.internalStart = i + 1;
                return true;
            }
            if (i - dataContext.internalStart + 1/* reserve one symbol for the ';' */ >= helper.getMaxSymbolsNumber()) {
                throw new IllegalStateException(String.format("Detected invalid encoded entity of type %s at the "
                    + "decorated symbol stream - it contains at least %d symbols though maximum "
                    + "allowed number for entity of type %s is %d. Found data: '%s'",
                    type, i - startOffsetToUse, type, helper.getMaxSymbolsNumber(), rawDataSeq(dataContext, i)));
            }
            code = helper.processSymbol(dataContext.internalBuffer[i], code);
            if (code < 0) {
                throw new IllegalStateException(String.format("Detected invalid encoded entity of type %s at the "
                    + "decorated symbol stream - it contains at least one invalid symbols = '%c'. Found data: '%s'",
                    type, dataContext.internalBuffer[i], rawDataSeq(dataContext, i)));
            }
        }
        return false;
    }

    /**
     * Allows to check if given offset is within <code>'raw data'</code> buffer managed by the given data holder.
     *
     * @param dataContext       target data holder
     * @param offset            target offset to check
     * @return                  <code>true</code> if given offset is valid for the the target
     *                          {@link DataContext#internalBuffer 'raw data' buffer}, i.e. not less than
     *                          its {@link DataContext#internalStart start offset} and less than its
     *                          {@link DataContext#internalEnd end offset}
     */
    private static boolean offsetInBounds(DataContext dataContext, int offset) {
        return offset >= dataContext.internalStart && offset < dataContext.internalEnd;
    }

    /**
     * Allows to build string that wraps {@link DataContext#internalBuffer 'raw data' buffer}'s region
     * [{@link DataContext#internalStart}; end).
     *
     * @param dataContext       target data holder
     * @param end               end offset to use for the target string construction from the
     *                          <code>'raw data'</code> buffer
     * @return                  string that wraps {@link DataContext#internalBuffer 'raw data' buffer}'s region
     *                          [{@link DataContext#internalStart}; end)
     */
    private static String rawDataSeq(DataContext dataContext, int end) {
        return new String(dataContext.internalBuffer, dataContext.internalStart, end - dataContext.internalStart);
    }

    /**
     * Defines contract for entity encoding type-specific functionality.
     */
    private interface DecodingHelper {

        /**
         * Process given symbol of the target encoded HTML entity.
         * <p/>
         * There is a possible case that encoded value is a unicode symbols value, hence, given <code>'code'</code>
         * defines a number decoded from the entity symbols so far.
         *
         * @param c                 target symbol to process
         * @param code              numeric value decoded from the entity symbols so far if any
         * @return                  non-negative numeric value if given symbol is successfully decoded;
         *                          negative value if given symbol is inconsistent with the entity encoding type
         */
        int processSymbol(char c, int code);

        /**
         * Decodes HTML entity or NCR assuming that encoded data is located at
         * {@link DataContext#internalBuffer 'raw data' buffer} at
         * <code>[{@link DataContext#internalStart}; offset]</code> range.
         *
         * @param dataContext       target data holder
         * @param offset            index of the last encoded entity symbols at the
         *                          {@link DataContext#internalBuffer 'raw data' buffer}
         * @param code              numeric value encoded at the target entity symbols if any
         * @return                  decoded unicode symbol
         */
        char decode(DataContext dataContext, int offset, int code);

        /**
         * @return      maximum possible number of symbols for the target entity type
         */
        int getMaxSymbolsNumber();
    }

    private static class DecimalDecodingHelper implements DecodingHelper {

        private static final int MAX_SYMBOLS_NUMBER
            = String.format("&#%s;", Integer.toString(Character.MAX_VALUE)).length();

        @Override
        public int processSymbol(char c, int code) {
            if (isDecimalDigit(c)) {
                return code * 10 + toNumber(c);
            }
            return -1;
        }

        @Override
        public char decode(DataContext dataContext, int offset, int code) {
            return (char) code;
        }

        @Override
        public int getMaxSymbolsNumber() {
            return MAX_SYMBOLS_NUMBER;
        }
    }

    private static class HexDecodingHelper implements DecodingHelper {

        private static final int MAX_SYMBOLS_NUMBER
            = String.format("&#x%s;", Integer.toHexString(Character.MAX_VALUE)).length();

        @Override
        public int processSymbol(char c, int code) {
            if (isHexDigit(c)) {
                return code * 16 + toNumber(c);
            }
            return -1;
        }

        @Override
        public char decode(DataContext dataContext, int offset, int code) {
            return (char) code;
        }

        @Override
        public int getMaxSymbolsNumber() {
            return MAX_SYMBOLS_NUMBER;
        }
    }

    private static class CharacterDecodingHelper implements DecodingHelper {

        private static final int MAX_SYMBOLS_NUMBER;
        static {
            int max = -1;
            for (CharSequence name : ENTITIES.keySet()) {
                max = Math.max(max, name.length());
            }
            MAX_SYMBOLS_NUMBER = max + 2 /* for '&' and ';' signs. */;
        }

        private final CharArrayCharSequence key = new CharArrayCharSequence();

        @Override
        public int processSymbol(char c, int code) {
            // Don't bother with invalid symbol because there is a rather small limit to the max
            // possible symbols number.
            return 0;
        }

        @Override
        public char decode(DataContext dataContext, int offset, int code) {
            key.updateState(
                dataContext.internalBuffer,
                dataContext.internalStart + 1 /* for '&' symbol */,
                offset // Assuming that offset points to entity closing symbol (';')
            );
            Character result = ENTITIES.get(key);
            if (result == null) {
                throw new IllegalStateException(String.format("Can't decode html entity '%s'. Reason: no mapping "
                    + "is registered for it. Available mappings: %s", key, ENTITIES));
            }

            return result;
        }

        @Override
        public int getMaxSymbolsNumber() {
            return MAX_SYMBOLS_NUMBER;
        }
    }

}
