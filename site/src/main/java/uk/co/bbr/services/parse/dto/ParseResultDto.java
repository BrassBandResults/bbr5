package uk.co.bbr.services.parse.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.people.dao.PersonDao;

@Getter
@Setter
public class ParseResultDto implements NameTools {

    private boolean parseSuccess;

    private String rawPosition;
    private String rawBandName;
    private String rawConductorName;
    private String rawDraw;
    private String rawPoints;

    private BandDao matchedBand;
    private PersonDao matchedConductor;

    public void setRawBandName(String value) {

        if (value == null) {
            this.rawBandName = null;
            return;
        }
        this.rawBandName = simplifyBandName(value);
    }

    public void setRawConductorName(String value) {

        if (value == null) {
            this.rawConductorName = null;
            return;
        }
        this.rawConductorName = simplifyPersonFullName(value);
    }

    public boolean isMatchSuccess() {
        return this.matchedBand != null && this.matchedConductor != null;
    }
}
