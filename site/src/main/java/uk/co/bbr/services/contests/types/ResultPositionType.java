package uk.co.bbr.services.contests.types;

import java.util.stream.Stream;

public enum ResultPositionType {
    RESULT("R", "Result Known", "result.known"),
    UNKNOWN("U", "Result Unknown", "result.unknown"),
    WITHDRAWN("W", "Withdrawn", "result.withdrawn"),
    DISQUALIFIED("D", "Disqualified", "result.disqualified"),
    ;

    private final String code;
    private final String displayName;
    private final String translationKey;

    ResultPositionType(String code, String displayName, String translationKey) {
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

    public static ResultPositionType fromCode(String code) {
        return Stream.of(ResultPositionType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
