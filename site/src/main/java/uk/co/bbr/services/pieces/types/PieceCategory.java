package uk.co.bbr.services.pieces.types;

import java.util.stream.Stream;

public enum PieceCategory {
    TEST_PIECE("T", "piece.test-piece"),
    MARCH("M", "piece.march"),
    HYMN("H", "piece.hymn"),
    ENTERTAINMENT("E", "piece.entertainment"),
    OTHER("O", "piece.other"),
    ;

    private final String code;
    private final String translationKey;

    PieceCategory(String code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static PieceCategory fromCode(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(PieceCategory.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
