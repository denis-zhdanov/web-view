package org.denis.webview.syntax.logic;

import java.util.Arrays;

/**
 * @author Denis Zhdanov
 * @since Jul 13, 2010
 */
public class Tuple {

    private final Object[] data;

    /**
     * Constructs new <code>Tuple</code> object with the given data.
     *
     * @param data      data to use within the current tuple
     */
    public Tuple(Object ... data) {
        this.data = Arrays.copyOf(data, data.length);
    }

    /**
     * Constructs new <code>Tuple</code> object which data is the same data used at the given tuple plus
     * all data given at the second argument. I.e. data from the given tuple has lower indexes than the indexes
     * used for the data provided at the second argument.
     *
     * @param tuple     tuple which data should be used within the current tuple
     * @param data      new data
     */
    public Tuple(Tuple tuple, Object ... data) {
        this.data = new Object[tuple.data.length + data.length];
        System.arraycopy(tuple.data, 0, this.data, 0, tuple.data.length);
        System.arraycopy(data, 0, this.data, tuple.data.length, data.length);
    }

    /**
     * Allows to get data at specified index.
     * <p/>
     * <b>Note:</b> it's assumed that the caller knows exactly what kind of data is stored for the given
     * index because no compile-time check is performed here. If the caller uses wrong type,
     * <code>ClassCastException</code> occurs.
     *
     * @param index     index of the interested data
     * @return          data stored at specified index
     * @throws IllegalArgumentException     if given index is out of range
     * @param <T>       implied target data type
     */
    @SuppressWarnings({"unchecked"})
    public <T> T get(int index) throws IllegalArgumentException {
        if (index < 0 || index >= data.length) {
            throw new IllegalArgumentException(String.format("Can't retrieve data for the given index (%d). "
                    + "Reason: the index is out of allowed range - [0; %d]", index, data.length));
        }
        return (T) data[index];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @SuppressWarnings({"SimplifiableIfStatement"})
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Arrays.equals(data, ((Tuple)o).data);
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}