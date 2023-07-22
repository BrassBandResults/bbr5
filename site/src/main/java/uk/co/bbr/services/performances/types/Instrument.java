package uk.co.bbr.services.performances.types;

import java.util.stream.Stream;

public enum Instrument {
    CONDUCTOR(1, "instrument.conductor"),
    PRINCIPAL_CORNET(2, "instrument.principal-cornet"),
    FRONT_ROW_CORNET(3, "instrument.front-row-cornet"),
    SOPRANO_CORNET(4, "instrument.soprano-cornet"),
    REPIANO_CORNET(5, "instrument.repiano-cornet"),
    SECOND_CORNET(6, "instrument.second-cornet"),
    THIRD_CORNET(7, "instrument.third-cornet"),
    BACK_ROW_CORNET(8, "instrument.back-row-cornet"),
    FLUGEL_HORN(9, "instrument.flugel-horn"),
    SOLO_HORN(10, "instrument.solo-horn"),
    FIRST_HORN(11, "instrument.first-horn"),
    SECOND_HORN(12, "instrument.second-horn"),
    SOLO_BARITONE(13, "instrument.solo-baritone"),
    SECOND_BARITONE(14, "instrument.second-baritone"),
    SOLO_EUPH(15, "instrument.solo-euph"),
    SECOND_EUPH(16, "instrument.second-euph"),
    SOLO_TROM(17, "instrument.solo-trom"),
    SECOND_TROM(18, "instrument.second-trom"),
    BASS_TROM(19, "instrument.bass-trom"),
    SOLO_EB_BASS(20, "instrument.solo-eb-bass"),
    SECOND_EB_BASS(21, "instrument.second-eb-bass"),
    SOLO_BB_BASS(22, "instrument.solo-bb-bass"),
    SECOND_BB_BASS(23, "instrument.second-bb-bass"),
    PERCUSSION(24, "instrument.percussion"),
    TUNED_PERCUSSION(25, "instrument.tuned-percussion"),
    KIT_PERCUSSION(26, "instrument.kit-percussion"),
    EB_BASS(27, "instrument.eb-bass"),
    BB_BASS(28, "instrument.bb-bass"),
    CORNET(29, "instrument.cornet"),
    TIMPANI(30, "instrument.timpani"),
    ;

    private final int code;
    private final String translationKey;

    Instrument(int code, String translationKey) {
        this.code = code;
        this.translationKey = translationKey;
    }

    public int getCode() {
        return this.code;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static Instrument fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Stream.of(Instrument.values())
                .filter(c -> c.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
