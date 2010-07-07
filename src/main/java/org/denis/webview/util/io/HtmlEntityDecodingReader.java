package org.denis.webview.util.io;

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
    }

    public HtmlEntityDecodingReader(Reader in) {
        super(in);
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
}
