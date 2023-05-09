package uk.co.bbr.services.parse.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.parse.types.ParseOutcome;
import uk.co.bbr.services.people.dao.PersonDao;

@Getter
@Setter
public class ParseResultDto implements NameTools {

    private ParseOutcome outcome = ParseOutcome.RED;

    private String rawPosition;
    private String rawBandName;
    private String rawConductorName;
    private String rawDraw;
    private String rawPoints;

    private BandDao matchedBand;
    private PersonDao matchedConductor;

    public void setRawBandName(String value) {
        this.rawBandName = simplifyBandName(value);
        this.outcome = ParseOutcome.AMBER;
    }

    public void setRawConductorName(String value) {
        this.rawConductorName = simplifyPersonFullName(value);
    }

    public void setMatchedBand(BandDao matchedBand) {
        this.matchedBand = matchedBand;
        if (this.matchedBand != null && this.matchedConductor != null) {
            this.outcome = ParseOutcome.GREEN;
        }
    }
}
