package uk.co.bbr.services.results.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.results.types.ParseOutcome;
import uk.co.bbr.services.people.dao.PersonDao;

import java.util.Optional;

@Getter
@Setter
public class ParseResultDto implements NameTools {

    private ParseOutcome outcome = ParseOutcome.RED_FAILED_PARSE;

    private String rawLine;

    private String rawPosition;
    private String rawBandName;
    private String rawConductorName;
    private Integer rawDraw;
    private String rawPoints;

    private String matchedBandSlug;
    private String matchedBandName;
    private String matchedConductorSlug;
    private String matchedConductorName;


    public void setRawBandName(String value) {
        this.rawBandName = simplifyBandName(value);
        this.outcome = ParseOutcome.AMBER_PARSE_SUCCEEDED;
    }

    public void setRawConductorName(String value) {
        this.rawConductorName = simplifyPersonFullName(value);
    }

    public void setMatchedBand(String matchedBandSlug, String matchedBandName) {
        this.matchedBandSlug = matchedBandSlug;
        this.matchedBandName = matchedBandName;
        if (this.matchedBandSlug != null && this.matchedConductorSlug != null) {
            this.outcome = ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE;
        }
    }


    public void setMatchedConductor(String matchedConductorSlug, String matchedConductorName) {
        this.matchedConductorSlug = matchedConductorSlug;
        this.matchedConductorName = matchedConductorName;
        if (this.matchedBandSlug != null && this.matchedConductorSlug != null) {
            this.outcome = ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE;
        }
    }

    public ContestResultDao buildContestResult(ContestEventDao contestEvent, BandService bandService, PersonService personService) {
        if (this.outcome != ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE) {
            return null;
        }

        Optional<BandDao> matchedBand = bandService.fetchBySlug(this.matchedBandSlug);
        if (matchedBand.isEmpty()) {
            return null;
        }

        Optional<PersonDao> matchedConductor = personService.fetchBySlug(this.matchedConductorSlug);
        if (matchedConductor.isEmpty()) {
            return null;
        }

        ContestResultDao contestResult = new ContestResultDao();
        contestResult.setContestEvent(contestEvent);
        contestResult.setPosition(this.rawPosition);
        contestResult.setBand(matchedBand.get());
        contestResult.setBandName(this.rawBandName);
        contestResult.setConductor(matchedConductor.get());
        contestResult.setOriginalConductorName(this.rawConductorName);
        contestResult.setDraw(this.rawDraw);

        return contestResult;
    }

    public boolean isRed() {
        return this.outcome.equals(ParseOutcome.RED_FAILED_PARSE);
    }

    public boolean isAmber() {
        return this.outcome.equals(ParseOutcome.AMBER_PARSE_SUCCEEDED);
    }

    public boolean isGreen() {
        return this.outcome.equals(ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE);
    }
}
