package uk.co.bbr.services.parse.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.parse.types.ParseOutcome;
import uk.co.bbr.services.people.dao.PersonDao;

@Getter
@Setter
public class ParseResultDto implements NameTools {

    private ParseOutcome outcome = ParseOutcome.RED_FAILED_PARSE;

    private String rawPosition;
    private String rawBandName;
    private String rawConductorName;
    private Integer rawDraw;
    private String rawPoints;

    private BandDao matchedBand;
    private PersonDao matchedConductor;

    public void setRawBandName(String value) {
        this.rawBandName = simplifyBandName(value);
        this.outcome = ParseOutcome.AMBER_PARSE_SUCCEEDED;
    }

    public void setRawConductorName(String value) {
        this.rawConductorName = simplifyPersonFullName(value);
    }

    public void setMatchedBand(BandDao matchedBand) {
        this.matchedBand = matchedBand;
        if (this.matchedBand != null && this.matchedConductor != null) {
            this.outcome = ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE;
        }
    }


    public void setMatchedConductor(PersonDao matchedConductor) {
        this.matchedConductor = matchedConductor;
        if (this.matchedBand != null && this.matchedConductor != null) {
            this.outcome = ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE;
        }
    }

    public ContestResultDao buildContestResult(ContestEventDao contestEvent) {
        if (this.outcome != ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE) {
            return null;
        }

        ContestResultDao contestResult = new ContestResultDao();
        contestResult.setContestEvent(contestEvent);
        contestResult.setPosition(this.rawPosition);
        contestResult.setBand(this.matchedBand);
        contestResult.setConductor(this.matchedConductor);
        contestResult.setDraw(this.rawDraw);

        return contestResult;
    }
}
