package uk.co.brassbandresults.extract.json;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import uk.co.brassbandresults.extract.data.ContestResultData;
import uk.co.brassbandresults.extract.data.PieceData;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonPropertyOrder(alphabetic = true)
public class ContestResultJson {

    private final String bandName;
    private final String bandSlug;
    private final String competedAs;
    private final String resultPositionType;
    private final String resultPosition;
    private final String draw;
    private final String drawSecondPart;
    private final String drawThirdPart;
    private final String pointsTotal;
    private final String pointsFirstPart;
    private final String pointsSecondPart;
    private final String pointsThirdPart;
    private final String pointsFourthPart;
    private final String pointsFifthPart;
    private final String pointsPenalty;
    private final String conductor1Slug;
    private final String conductor1FirstNames;
    private final String conductor1Surname;
    private final String conductor2Slug;
    private final String conductor2FirstNames;
    private final String conductor2Surname;
    private final String conductor3Slug;
    private final String conductor3FirstNames;
    private final String conductor3Surname;
    private final String notes;
    private final String bandRegionSlug;
    private final String bandRegionName;
    private final String bandRegionCountryCode;
    private final String resultId;
    private List<PieceJson> ownChoice = null;

    public ContestResultJson(ContestResultData data) {
        this.bandName = data.getBandName();
        this.bandSlug = data.getBandSlug();
        this.competedAs = data.getCompetedAs();
        this.resultPositionType = data.getResultPositionType();
        this.resultPosition = data.getResultPosition();
        this.draw = data.getDraw();
        this.drawSecondPart = data.getDrawSecondPart();
        this.drawThirdPart = data.getDrawThirdPart();
        this.pointsTotal = data.getPointsTotal();
        this.pointsFirstPart = data.getPointsFirstPart();
        this.pointsSecondPart = data.getPointsSecondPart();
        this.pointsThirdPart = data.getPointsThirdPart();
        this.pointsFourthPart = data.getPointsFourthPart();
        this.pointsFifthPart = data.getPointsFifthPart();
        this.pointsPenalty = data.getPointsPenalty();
        this.conductor1Slug = data.getConductor1Slug();
        this.conductor1FirstNames = data.getConductor1FirstNames();
        this.conductor1Surname = data.getConductor1Surname();
        this.conductor2Slug = data.getConductor2Slug();
        this.conductor2FirstNames = data.getConductor2FirstNames();
        this.conductor2Surname = data.getConductor2Surname();
        this.conductor3Slug = data.getConductor3Slug();
        this.conductor3FirstNames = data.getConductor3FirstNames();
        this.conductor3Surname = data.getConductor3Surname();
        this.notes = data.getNotes();
        this.bandRegionSlug = data.getBandRegionSlug();
        this.bandRegionName = data.getBandRegionName();
        this.bandRegionCountryCode = data.getBandRegionCountryCode();
        this.resultId = data.getResultId();
    }

    public void addPiece(PieceData eachPiece) {
        if (this.ownChoice == null) {
            this.ownChoice = new ArrayList<>();
        }
        this.ownChoice.add(new PieceJson(eachPiece));
    }
}
