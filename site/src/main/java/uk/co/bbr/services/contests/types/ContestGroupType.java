package uk.co.bbr.services.contests.types;

import java.util.stream.Stream;

public enum ContestGroupType {
    NORMAL("G",  "group-type.normal"),
    WHIT_FRIDAY("W", "group-type.whit-friday"),
    ;

    private final String code;
    private final String translationKey;

    ContestGroupType(String code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static ContestGroupType fromCode(String code) {
        return Stream.of(ContestGroupType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
