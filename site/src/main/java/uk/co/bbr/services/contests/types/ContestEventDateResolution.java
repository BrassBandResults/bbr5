package uk.co.bbr.services.contests.types;

import java.util.stream.Stream;

public enum ContestEventDateResolution {
    EXACT_DATE("D", "date-resolution.exact"),
    MONTH_AND_YEAR("M",  "date-resolution.month-year"),
    YEAR("Y",  "date-resolution.year"),
    ;

    private final String code;
    private final String translationKey;

    ContestEventDateResolution(String code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static ContestEventDateResolution fromCode(String code) {
        return Stream.of(ContestEventDateResolution.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
