package uk.co.bbr.services.events.types;

import java.util.stream.Stream;

public enum ResultPositionType {
    RESULT("R", "result.known"),
    UNKNOWN("U", "result.unknown"),
    WITHDRAWN("W", "result.withdrawn"),
    DISQUALIFIED("D", "result.disqualified"),
    ;

    private final String code;
    private final String translationKey;

    ResultPositionType(String code, String translationKey) {
        this.code = code;
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
