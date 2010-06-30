package org.denis.webview.util.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

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

    public HtmlEntityDecodingReader(Reader in) {
        super(in);
    }

    @Override
    protected int copy(DataContext dataContext) throws IllegalStateException {
        int result = 0;
        for (
            int externalBufferOffset = dataContext.externalOffset,
                externalBufferMaxOffset = dataContext.externalOffset + dataContext.externalLength;
            dataContext.internalStart < dataContext.internalEnd && externalBufferOffset < externalBufferMaxOffset;
            ++dataContext.internalStart, ++result)
        {
            if (dataContext.internalBuffer[dataContext.internalStart] != '&') {
                dataContext.externalBuffer[externalBufferOffset++]
                    = dataContext.internalBuffer[dataContext.internalStart];
                continue;
            }

            // Current internal buffer offset points to the entity start.
            EntityEncodingType entityType = parseEntityType(dataContext);
            if (entityType == null) {
                break;
            }

            switch (entityType) {
                case DECIMAL: decodeDecimalEntity(dataContext); break;
                case HEX: decodeHexEntity(dataContext); break;
                case CHARACTER: decodeCharacterEntity(dataContext); break;
                default: //TODO den throw exception
            }
        }
        return result;

    }

    /**
     * Assumes that given {@link DataContext#internalBuffer 'raw data' buffer} holds <code>'&amp;'</code> at
     * position identified by {@link DataContext#internalStart start offset} and tries to determine type
     * of the entity encoding.
     *
     * @param dataContext       target data holder
     * @return                  type of encoded entity contained at <code>'raw data'</code> buffer if
     *                          it's possible determine the one; <code>null</code> if <code>'raw data'</code>
     *                          buffer doesn't contain complete entity data
     * @throws IllegalStateException        if it's encountered that given <code>'raw'</code> data is invalid 
     */
    private EntityEncodingType parseEntityType(DataContext dataContext) throws IllegalStateException {
        //TODO den impl
    }

    /**
     * Decodes data from the {@link DataContext#internalBuffer 'raw data buffer'} assuming that it's encoding type
     * is {@link EntityEncodingType#DECIMAL} and puts it to {@link DataContext#externalBuffer target buffer}.
     * <p/>
     * This method is responsible for assigning correct new values to buffer offsets if necessary.
     *
     * @param dataContext       target data holder
     */
    private void decodeDecimalEntity(DataContext dataContext) {
        //TODO den impl
    }

    /**
     * Decodes data from the {@link DataContext#internalBuffer 'raw data buffer'} assuming that it's encoding type
     * is {@link EntityEncodingType#HEX} and puts it to {@link DataContext#externalBuffer target buffer}.
     * <p/>
     * This method is responsible for assigning correct new values to buffer offsets if necessary.
     *
     * @param dataContext       target data holder
     */
    private void decodeHexEntity(DataContext dataContext) {
        //TODO den impl
    }

    /**
     * Decodes data from the {@link DataContext#internalBuffer 'raw data buffer'} assuming that it's encoding type
     * is {@link EntityEncodingType#CHARACTER} and puts it to {@link DataContext#externalBuffer target buffer}.
     * <p/>
     * This method is responsible for assigning correct new values to buffer offsets if necessary.
     *
     * @param dataContext       target data holder
     */
    private void decodeCharacterEntity(DataContext dataContext) {
        //TODO den impl
    }
}
