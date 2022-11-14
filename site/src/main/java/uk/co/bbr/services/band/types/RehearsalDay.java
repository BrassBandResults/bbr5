package uk.co.bbr.services.band.types;

import java.util.stream.Stream;

public enum RehearsalDay {
    SUNDAY(0, "Sunday"),
    MONDAY(1, "Monday"),
    TUESDAY(2, "Tuesday"),
    WEDNESDAY(3, "Wednesday"),
    THURSDAY(4, "Thursday"),
    FRIDAY(5, "Friday"),
    SATURDAY(6, "Saturday"),
    ;

    private final int id;
    private final String displayName;

    RehearsalDay(int id, String displayName){
        this.id = id;
        this.displayName = displayName;
    }

    public int getCode() {
        return this.id;
    }
    public String getDisplayName() { return this.displayName; }

    public static RehearsalDay fromCode(int code) {
        return Stream.of(RehearsalDay.values())
                .filter(c -> c.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static RehearsalDay fromName(String name) {
        return Stream.of(RehearsalDay.values())
                .filter(c -> c.getDisplayName().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
