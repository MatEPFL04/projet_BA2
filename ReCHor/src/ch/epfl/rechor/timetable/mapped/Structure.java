package ch.epfl.rechor.timetable.mapped;
import ch.epfl.rechor.Preconditions;
import java.util.Objects;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * The class helps describing the structure of the flattened representation.
 * Two attributes are needed:
 * - firstBytes, an array that store the position of the first byte of each data type, that is stored in the structure
 * - structureSize, the number of bytes that are used to describe all the components of one object
 *  stored in the array
 */

public class Structure {

    private short [] firstBytes;
    private short structureSize;

    /**
     *
     * @param fields, the fields given to Structure: each field has a number of bytes used to store the field,
     *                and a field index in the structure.
     */

    public Structure (Field ... fields) {

        firstBytes = new short[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Preconditions.checkArgument(fields[i].index == i);
        }

        for (int i = 0; i < fields.length; i++) {
            FieldType currentFieldType = fields[i].type;
            switch (currentFieldType) {
                case U8 -> {
                    firstBytes[i] = structureSize;
                    structureSize++;
                }
                case U16 -> {
                    firstBytes[i] = structureSize;
                    structureSize += 2;
                }
                case S32 -> {
                    firstBytes[i] = structureSize;
                    structureSize += 4;
                }

            }
        }

    }

    /**
     * method used to get the total number of bytes required to store the information about one object,
     * inside an array containing all of these objects
     * @return size of the structure
     */

    public int totalSize () {
        return structureSize;
    }

    /**
     *
     * @param fieldIndex, the index related to the field for which we evaluate the byte position
     * @param elementIndex, the index of the element that will be stored in the array:
     *                      for instance: 9, to get access to the tenth station
     * @return the byte position of the first byte of a field, of a particular element (elementIndex)
     */

    public int offset(int fieldIndex, int elementIndex) {

        return elementIndex * totalSize() + firstBytes[fieldIndex];
    }

    /**
     * the only three fieldType, i.e. number representations that will be used in ReCHor
     */

    public enum FieldType {
        U8,
        U16,
        S32;
    }

    /**
     *
     * @param index the "rank" of the field in the structure
     * @param type the type used to store the data
     */
    public record Field (int index, FieldType type) {
        public Field {
            Objects.requireNonNull(type);
        }
    }

    /**
     * method created to quickly instantiate new fields
     * @param index the "rank" of the field in the structure
     * @param type the type used to store the data
     * @return a new Field with the given attributes.
     */
    public static Field field (int index, FieldType type) {
        return new Field(index,type);
    }
}

