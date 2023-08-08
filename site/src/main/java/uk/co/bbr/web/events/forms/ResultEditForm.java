package uk.co.bbr.web.events.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Getter
@Setter
public class ResultEditForm {
    private Integer position;
    private boolean withdrawn;
    private boolean disqualified;
    private String competedAs;
    private String bandName;
    private String bandSlug;
    private Integer draw;
    private Integer drawTwo;
    private Integer drawThree;
    private String pointsOne;
    private String pointsTwo;
    private String pointsThree;
    private String pointsFour;
    private String pointsPenalty;
    private String pointsTotal;
    private String originalConductorName;
    private String conductorName;
    private String conductorSlug;
    private String conductorTwoName;
    private String conductorTwoSlug;
    private String conductorThreeName;
    private String conductorThreeSlug;
    private String notes;

    public ResultEditForm() {
        super();
    }

    public ResultEditForm(ContestResultDao result) {
        assertNotNull(result);

        switch (result.getResultPositionType()) {
            case RESULT:
                this.position = result.getPosition();
                this.withdrawn = false;
                this.disqualified = false;
                break;
            case UNKNOWN:
                this.position = null;
                this.withdrawn = false;
                this.disqualified = false;
                break;
            case WITHDRAWN:
                this.position = null;
                this.withdrawn = true;
                this.disqualified = false;
                break;
            case DISQUALIFIED:
                this.position = null;
                this.withdrawn = false;
                this.disqualified = true;
                break;
        }

        this.competedAs = result.getBandName();
        this.bandName = result.getBand().getName();
        this.bandSlug = result.getBand().getSlug();

        this.notes = result.getNotes();

        this.draw = result.getDraw();
        this.drawTwo = result.getDrawSecond();
        this.drawThree = result.getDrawThird();

        this.pointsOne = result.getPointsFirst();
        this.pointsTwo = result.getPointsSecond();
        this.pointsThree = result.getPointsThird();
        this.pointsFour = result.getPointsFourth();
        this.pointsPenalty = result.getPointsPenalty();
        this.pointsTotal = result.getPointsTotal();

        this.originalConductorName = result.getOriginalConductorName();
        if (result.getConductor() != null) {
            this.conductorName = result.getConductor().getName();
            this.conductorSlug = result.getConductor().getSlug();
        }

        if (result.getConductorSecond() != null) {
            this.conductorTwoName = result.getConductorSecond().getName();
            this.conductorTwoSlug = result.getConductorSecond().getSlug();
        }

        if (result.getConductorThird() != null) {
            this.conductorThreeName = result.getConductorThird().getName();
            this.conductorThreeSlug = result.getConductorThird().getSlug();
        }
    }

    public void validate(BindingResult bindingResult) {
        if (this.bandName == null || this.bandName.strip().length() == 0) {
            bindingResult.addError(new ObjectError("bandName", "page.result-edit.errors.band-required"));
        }
    }
}
