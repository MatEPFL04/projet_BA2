package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.nio.ByteBuffer;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */

public class StructuredBuffer {

    private final ByteBuffer buffer;
    private final Structure structure;

    public StructuredBuffer(Structure structure, ByteBuffer buffer) {
        Preconditions.checkArgument(buffer.capacity() % structure.totalSize() == 0);

        this.structure = structure;
        this.buffer = buffer;
    }

    /**
     * The number of elements are the number of bytes divided by the number of bytes
     * of a certain structure.
     * @return the number of elements of the "array".
     */
    public int size() {
        return buffer.capacity()/structure.totalSize();
    }

    /**
     * Check if the element index given surpasses the size of the buffer
     *
     * @param elementIndex
     */
    private void checkElementIndex(int elementIndex) {
        if(elementIndex < 0 || elementIndex >= size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     *
     * @param fieldIndex
     * @param elementIndex
     * @return
     */
    public int getU8(int fieldIndex, int elementIndex) {
        checkElementIndex(elementIndex);
        byte U8 = buffer.get(structure.offset(fieldIndex, elementIndex));
        return Byte.toUnsignedInt(U8);
    }

    /**
     *
     * @param fieldIndex
     * @param elementIndex
     * @return
     */
    public int getU16(int fieldIndex, int elementIndex) {
        checkElementIndex(elementIndex);
        short U16 = buffer.getShort(structure.offset(fieldIndex, elementIndex));
        return Short.toUnsignedInt(U16);
    }

    /**
     *
     * @param fieldIndex
     * @param elementIndex
     * @return
     */
    public int getS32(int fieldIndex, int elementIndex) {
        checkElementIndex(elementIndex);

        return buffer.getInt(structure.offset(fieldIndex, elementIndex));
    }
}
