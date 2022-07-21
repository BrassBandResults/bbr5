package uk.co.bbr.services.band.types;

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
}
