package uk.co.brassbandresults.extract.json;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import uk.co.brassbandresults.extract.data.ContestEventData;

@Getter
@JsonPropertyOrder(alphabetic = true)
public class ContestTypeJson {
    private final String contestTypeName;
    private final String contestTypeSlug;
    private final String drawOneTitle;
    private final String drawTwoTitle;
    private final String drawThreeTitle;
    private final String pointsTotalTitle;
    private final String pointsOneTitle;
    private final String pointsTwoTitle;
    private final String pointsThreeTitle;
    private final String pointsFourTitle;
    private final String pointsFiveTitle;
    private final String pointsPenaltyTitle;
    private final boolean hasTestPiece;
    private final boolean hasOwnChoice;
    private final boolean hasEntertainments;
    public ContestTypeJson(ContestEventData event) {
        this.contestTypeName = event.getContestTypeName();
        this.contestTypeSlug = event.getContestTypeSlug();
        this.drawOneTitle = event.getDrawOneTitle();
        this.drawTwoTitle = event.getDrawTwoTitle();
        this.drawThreeTitle = event.getDrawThreeTitle();
        this.pointsTotalTitle = event.getPointsTotalTitle();
        this.pointsOneTitle = event.getPointsOneTitle();
        this.pointsTwoTitle = event.getPointsTwoTitle();
        this.pointsThreeTitle = event.getPointsThreeTitle();
        this.pointsFourTitle = event.getPointsFourTitle();
        this.pointsFiveTitle = event.getPointsFiveTitle();
        this.pointsPenaltyTitle = event.getPointsPenaltyTitle();
        this.hasTestPiece = event.isHasTestPiece();
        this.hasOwnChoice = event.isHasOwnChoice();
        this.hasEntertainments = event.isHasEntertainments();
    }
}
