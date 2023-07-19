package uk.co.bbr.services.security.types;

import java.util.stream.Stream;

public enum ContestHistoryVisibility {
    PUBLIC("O", "contest-history-visibility.public"),
    PRIVATE("P", "contest-history-visibility.private"),
    SITE_ONLY("S", "contest-history-visibility.site-only"),
    ;

    private final String code;
    private final String translationKey;

    ContestHistoryVisibility(String code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static ContestHistoryVisibility fromCode(String code) {
        if (code == null) {
            return ContestHistoryVisibility.SITE_ONLY;
        }
        return Stream.of(ContestHistoryVisibility.values())
                .filter(c -> c.getCode().equals(code.trim().toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
