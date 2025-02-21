package ch.epfl.rechor;



public final class Bits32_24_8 {
    private Bits32_24_8 () {}
    static int pack(int bits24, int bits8) {
        Preconditions.checkArgument(((bits24 >> 24) == 0) && ((bits8 >> 8) == 0));
        return (bits24 << 8) | bits8;

    }
    static int unpack24(int bits32) {
        return bits32 >> 8;

    }
    int unpack8(int bits32) {
        int mask = 0xFF;
        return bits32 & mask;
    }

}



