package uk.co.bbr.services.band.types;

import java.util.stream.Stream;

public enum RehearsalDay {
    SUNDAY(0, "Sunday", "day.sunday"),
    MONDAY(1, "Monday", "day.monday"),
    TUESDAY(2, "Tuesday", "day.tuesday"),
    WEDNESDAY(3, "Wednesday", "day.wednesday"),
    THURSDAY(4, "Thursday", "day.thursday"),
    FRIDAY(5, "Friday", "day.friday"),
    SATURDAY(6, "Saturday", "day.saturday"),
    ;

    private final int id;
    private final String displayName;
    private final String translationKey;

    RehearsalDay(int id, String displayName, String translationKey){
        this.id = id;
        this.displayName = displayName;
        this.translationKey = translationKey;
    }

    public int getCode() {
        return this.id;
    }
    public String getTranslationKey() { return this.translationKey; }

    public static RehearsalDay fromCode(int code) {
        return Stream.of(RehearsalDay.values())
                .filter(c -> c.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static RehearsalDay fromName(String name) {
        return Stream.of(RehearsalDay.values())
                .filter(c -> c.displayName.equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }


}
