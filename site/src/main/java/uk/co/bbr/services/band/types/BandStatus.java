package uk.co.bbr.services.band.types;

import java.util.stream.Stream;

public enum BandStatus {
    EXTINCT(0, "Extinct"),
    COMPETING(1, "Competing"),
    NON_COMPETING(2, "Non-competing"),
    YOUTH(3, "Youth"),
    SALVATION_ARMY(4, "Salvation Army"),
    WIND_BAND(5, "Now a Wind Band"),
    SCRATCH(6, "Scratch Band"),
    ;

    private final int id;
    private final String displayName;

    BandStatus(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getCode() {
        return this.id;
    }

    public static BandStatus fromCode(int code) {
        return Stream.of(BandStatus.values())
                .filter(c -> c.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
