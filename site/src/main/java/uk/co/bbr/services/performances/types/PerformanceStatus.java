package uk.co.bbr.services.performances.types;

import java.util.stream.Stream;

public enum PerformanceStatus {
    ACCEPTED("A", "performance-status.accepted"),
    PENDING("P", "performance-status.pending"),
    DECLINED("D", "performance-status.declined"),

    ;

    private final String code;
    private final String translationKey;

    PerformanceStatus(String code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static PerformanceStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(PerformanceStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
