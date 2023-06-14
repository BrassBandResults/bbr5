package uk.co.bbr.services.feedback.types;

import java.util.stream.Stream;

public enum FeedbackStatus {
    NEW("N", "feedback-status.new"),
    ;
    private final String code;
    private final String translationKey;

    FeedbackStatus(String code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static FeedbackStatus fromCode(String code) {
        return Stream.of(FeedbackStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
