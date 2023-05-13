package uk.co.bbr.services.contests.types;

import java.util.stream.Stream;

public enum TestPieceAndOr {
    AND("A", "and-or.and"),
    OR("O", "and-or.or"),
    ;

    private final String code;
    private final String translationKey;

    TestPieceAndOr(String code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static TestPieceAndOr fromCode(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(TestPieceAndOr.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
