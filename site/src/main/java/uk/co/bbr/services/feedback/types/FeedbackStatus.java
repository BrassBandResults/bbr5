package uk.co.bbr.services.feedback.types;

import java.util.stream.Stream;

public enum FeedbackStatus {
    NEW("N", "New"),
    OWNER("O", "Owner"),
    DONE("D", "Done"),
    INCONCLUSIVE("I", "Inconclusive"),
    SPAM("S", "Spam"),
    CLOSED("C", "Closed"),
    WITH_USER("U", "With User");
    private final String code;
    private final String name;

    FeedbackStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName(){
        return this.name;
    }

    public static FeedbackStatus fromCode(String code) {
        return Stream.of(FeedbackStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
