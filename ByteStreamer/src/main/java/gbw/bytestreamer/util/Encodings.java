package gbw.bytestreamer.util;

public enum Encodings {
    UTF_8(1),
    UTF_16(2),
    UTF_32(4),
    ISO_8859_1(1),
    ISO_8859_15(1),
    WINDOWS_1252(1),
    ASCII(1),
    EBCDIC(1),  // Size is set to 1 for simplicity, actual size varies
    SHIFT_JIS(2),
    BIG5(2),
    EUC_JP(1),  // Size is set to 1 for simplicity, actual size varies
    KOI8_R(1),
    ISO_2022(1);  // Size is set to 1 for simplicity, actual size varies
    public final int bytesPerChar;

    Encodings(int bytesPerCharacter) {
        this.bytesPerChar = bytesPerCharacter;
    }

}
