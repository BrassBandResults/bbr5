package uk.co.bbr.services.pieces.types;

import java.util.stream.Stream;

public enum PieceCategory {
    TEST_PIECE("T", "Test Piece", "piece.test-piece"),
    MARCH("M", "March", "piece.march"),
    HYMN("H", "Hymn", "piece.hymn"),
    ENTERTAINMENT("E", "Entertainment", "piece.entertainment"),
    OTHER("O", "Other", "piece.other"),
    ;

    private final String code;
    private final String displayName;
    private final String translationKey;

    PieceCategory(String code, String displayName, String translationKey) {
        this.code = code;
        this.displayName = displayName;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static PieceCategory fromCode(String code) {
        return Stream.of(PieceCategory.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static PieceCategory fromDescription(String description) {
        return Stream.of(PieceCategory.values())
                .filter(c -> c.displayName.equals(description))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
