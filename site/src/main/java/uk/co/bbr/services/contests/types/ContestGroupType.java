package uk.co.bbr.services.contests.types;

import java.util.stream.Stream;

public enum ContestGroupType {
    NORMAL("G", "Normal", "group-type.normal"),
    WHIT_FRIDAY("W", "Whit Friday", "group-type.whit-friday"),
    ;

    private final String code;
    private final String displayName;
    private final String translationKey;

    ContestGroupType(String code, String displayName, String translationKey) {
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

    public static ContestGroupType fromCode(String code) {
        return Stream.of(ContestGroupType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
