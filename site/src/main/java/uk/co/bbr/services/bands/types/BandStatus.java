package uk.co.bbr.services.bands.types;

import java.util.stream.Stream;

public enum BandStatus {
    EXTINCT(0, "Extinct", "status.extinct"),
    COMPETING(1, "Competing", "status.competing"),
    NON_COMPETING(2, "Non-competing", "status.non-competing"),
    YOUTH(3, "Youth", "status.youth"),
    SALVATION_ARMY(4, "Salvation Army", "status.salvation-army"),
    WIND_BAND(5, "Now a Wind Band", "status.wind-band"),
    SCRATCH(6, "Scratch Band", "status.scratch"),
    ;

    private final int id;
    private final String displayName;
    private final String translationKey;

    BandStatus(int id, String displayName, String translationKey) {
        this.id = id;
        this.displayName = displayName;
        this.translationKey = translationKey;
    }

    public int getCode() {
        return this.id;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public boolean isExtinct() {
        return this.getCode() == 0;
    }
    public boolean isNotExtinct() {
        return this.getCode() != 0;
    }

    public static BandStatus fromCode(int code) {
        return Stream.of(BandStatus.values())
                .filter(c -> c.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static BandStatus fromDescription(String description) {
        return Stream.of(BandStatus.values())
                .filter(c -> c.displayName.equals(description))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
