package uk.co.bbr.services.contests.types;

import java.util.stream.Stream;

public enum ResultAwardType {
    GOLD("G", "result-award.gold"),
    SILVER("S", "result-award.silver"),
    BRONZE("B",  "result-award.bronze"),
    MERIT("M", "result-award.merit"),
    ;

    private final String code;
    private final String translationKey;

    ResultAwardType(String code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static ResultAwardType fromCode(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(ResultAwardType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
