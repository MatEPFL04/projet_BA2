package ch.epfl.rechor.timetable.mapped;
import ch.epfl.rechor.Preconditions;
import java.util.Objects;

/**
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 * The class helps describing the structure of the flattened representation.
 */

public class Structure {

    private short [] firstBytes;
    private short structureSize;

    /**
     *
     * @param fields
     */

    public Structure (Field ... fields) {

        /**
         * @throws checkArgument to check if the indexes are in the right order
         */
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

    int totalSize () {
        return structureSize;
    }

    int offset(int fieldIndex, int elementIndex) {

        return elementIndex * totalSize() + firstBytes[fieldIndex];
    }

    public enum FieldType {
        U8,
        U16,
        S32;
    }
    public record Field (int index, FieldType type) {
        public Field {
            Objects.requireNonNull(type);
        }
    }
    public static Field field (int index, FieldType type) {
        return new Field(index,type);
    }
}

