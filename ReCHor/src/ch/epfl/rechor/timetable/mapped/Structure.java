package ch.epfl.rechor.timetable.mapped;
import ch.epfl.rechor.Preconditions;
import java.util.Objects;

public class Structure {

    private short [] firstBytes;
    private short structureSize;

    public Structure (Field ... fields) {
        firstBytes = new short[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Preconditions.checkArgument(fields[i].index == i);
        }

        for (int i = 0; i < fields.length; i++) {
            if (fields [i].type.equals(FieldType.U8)) {
                firstBytes[i] = structureSize;
                structureSize++;
            }
            else if (fields[i].type.equals(FieldType.U16)) {
                firstBytes[i] = structureSize;
                structureSize += 2;
            }
            else if (fields[i].type.equals(FieldType.S32)) {
                firstBytes[i] = structureSize;
                structureSize += 4;
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

