package uk.co.brassbandresults.extract.json;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import uk.co.brassbandresults.extract.data.AdjudicatorData;
import uk.co.brassbandresults.extract.data.ContestEventData;
import uk.co.brassbandresults.extract.data.ContestResultData;
import uk.co.brassbandresults.extract.data.PieceData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonPropertyOrder(alphabetic = true)
public class ContestEventJson {

    private final String contestSlug;
    private final String contestName;
    private final String contestNotes;
    private final String groupSlug;
    private final String groupName;
    private final String groupNotes;
    private final String regionSlug;
    private final String regionName;
    private final String regionCountryCode;
    private final String sectionName;
    private final String sectionSlug;
    private final String venueName;
    private final String venueSlug;
    private final String eventName;
    private final Date eventDate;
    private final String eventDateResolution;
    private final String eventNotes;
    private final boolean eventNoContestTookPlace;
    private final ContestTypeJson contestType;
    private final List<ContestResultJson> results = new ArrayList<>();
    private final List<AdjudicatorJson> adjudicators = new ArrayList<>();
    private List<PieceJson> setTests = null;

    public ContestEventJson(ContestEventData event) {
        this.contestSlug = event.getContestSlug();
        this.contestName = event.getContestName();
        this.contestNotes = event.getContestNotes();
        this.groupSlug = event.getGroupSlug();
        this.groupName = event.getGroupName();
        this.groupNotes = event.getGroupNotes();
        this.regionSlug = event.getRegionSlug();
        this.regionName = event.getRegionName();
        this.regionCountryCode = event.getRegionCountryCode();
        this.sectionName = event.getSectionName();
        this.sectionSlug = event.getSectionSlug();
        this.venueName = event.getVenueName();
        this.venueSlug = event.getVenueSlug();
        this.eventName = event.getEventName();
        this.eventDate = event.getEventDate();
        this.eventDateResolution = event.getEventDateResolution();
        this.eventNotes = event.getEventNotes();
        this.eventNoContestTookPlace = event.isEventNoContestTookPlace();
        this.contestType = new ContestTypeJson(event);
    }

    public void addResults(List<ContestResultData> resultData) {
        for (ContestResultData eachData : resultData) {
            this.results.add(new ContestResultJson(eachData));
        }
    }

    public void addAdjudicators(List<AdjudicatorData> adjudicatorData) {
        for (AdjudicatorData eachAdjudicator : adjudicatorData) {
            this.adjudicators.add(new AdjudicatorJson(eachAdjudicator));
        }
    }


    public void addSetTests(List<PieceData> setTests) {
        for (PieceData eachPiece : setTests) {
            if (this.setTests == null)   {
                this.setTests = new ArrayList<>();
            }
            this.setTests.add(new PieceJson(eachPiece));
        }
    }

    public void addOwnChoice(List<PieceData> ownChoice) {
        for (PieceData eachPiece : ownChoice) {
            for (ContestResultJson eachResult : this.results) {
                if (eachResult.getResultId().equals(eachPiece.getResultId())) {
                    eachResult.addPiece(eachPiece);
                    break;
                }
            }
        }
    }

    public int eventYear() {
        return this.eventDate.toLocalDate().getYear();
    }

    public int eventMonth() {
        return this.eventDate.toLocalDate().getMonth().getValue();
    }

    public boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public void writeData(String filePath) throws IOException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String json = new ObjectMapper()
            .setDateFormat(df)
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(this);

        File file = new File(filePath.substring(0, filePath.lastIndexOf("/")));
        file.mkdirs();

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(json.getBytes());
            fos.flush();
        }
    }


}
