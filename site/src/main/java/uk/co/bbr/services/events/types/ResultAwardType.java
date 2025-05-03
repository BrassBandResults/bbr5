package uk.co.bbr.services.events.types;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum ResultAwardType {
    PLATINUM("P", "result-award.platinum"),
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

    public static ResultAwardType fromCode(String code) {
        if (code == null) {
            return null;
        }
        if (code.trim().isEmpty()) {
            return null;
        }
        return Stream.of(ResultAwardType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
