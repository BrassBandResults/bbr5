package uk.co.bbr.services.events.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.events.types.ResultAwardType;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.tags.sql.dto.ContestTagSqlDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_result")
public class ContestResultDao extends AbstractDao implements NameTools {
    @Column(name="old_id")
    private String oldId;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_event_id")
    private ContestEventDao contestEvent;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="band_id")
    private BandDao band;

    @Column(name="band_name")
    private String bandName;

    @Column(name="result_position_type")
    @Setter
    private ResultPositionType resultPositionType = ResultPositionType.UNKNOWN;

    @Column(name="result_position")
    private Integer position;

    @Column(name="result_award")
    @Setter
    private ResultAwardType resultAward;

    @Column(name="draw")
    private Integer draw;

    @Column(name="draw_second")
    private Integer drawSecond;

    @Column(name="draw_third")
    private Integer drawThird;

    @Column(name="points_total")
    private String pointsTotal;

    @Column(name="points_first")
    private String pointsFirst;

    @Column(name="points_second")
    private String pointsSecond;

    @Column(name="points_third")
    private String pointsThird;

    @Column(name="points_fourth")
    private String pointsFourth;

    @Column(name="points_fifth")
    private String pointsFifth;

    @Column(name="points_penalty")
    private String pointsPenalty;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="conductor_id")
    private PersonDao conductor;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="conductor_two_id")
    private PersonDao conductorSecond;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="conductor_three_id")
    private PersonDao conductorThird;

    @Column(name="conductor_name")
    private String originalConductorName;

    @Column(name="notes")
    private String notes;

    @Transient
    @Setter
    private List<ContestResultPieceDao> pieces;

    @Transient
    @Setter
    private List<ContestTagSqlDto> tags = new ArrayList<>();

    @Transient
    @Setter
    private boolean canEdit = false;

    @Transient
    @Setter
    private boolean duplicateBandThisEvent = false;

    public void populateFrom(ContestResultDao result) {
        this.setOriginalConductorName(result.getOriginalConductorName());
        if (this.getConductor() == null && result.getConductor() != null) {
            this.setConductor(result.getConductor());
        }
        if (this.getConductorSecond() == null && result.getConductorSecond() != null) {
            this.setConductorSecond(result.getConductorSecond());
        }
        if (this.getConductorThird() == null && result.getConductorThird() != null) {
            this.setConductorThird(result.getConductorThird());
        }
        if (this.resultPositionType.equals(ResultPositionType.UNKNOWN) && !result.getResultPositionType().equals(ResultPositionType.UNKNOWN)) {
            this.resultPositionType = result.getResultPositionType();
        }
        if (this.position == null && result.getPosition() != null) {
            this.position = result.getPosition();
        }
        if (this.getDraw() == null && result.getDraw() != null) {
            this.setDraw(result.getDraw());
        }
        if (this.getDrawSecond() == null && result.getDrawSecond() != null) {
            this.setDrawSecond(result.getDrawSecond());
        }
        if (this.getDrawThird() == null && result.getDrawThird() != null) {
            this.setDrawThird(result.getDrawThird());
        }
        if (this.getNotes() == null && result.getNotes() != null) {
            this.setNotes(result.getNotes());
        }
        if (this.getPointsTotal() == null && result.getPointsTotal() != null) {
            this.setPointsTotal(result.getPointsTotal());
        }
        if (this.getPointsFirst() == null && result.getPointsFirst() != null) {
            this.setPointsFirst(result.getPointsFirst());
        }
        if (this.getPointsSecond() == null && result.getPointsSecond() != null) {
            this.setPointsSecond(result.getPointsSecond());
        }
        if (this.getPointsThird() == null && result.getPointsThird() != null) {
            this.setPointsThird(result.getPointsThird());
        }
        if (this.getPointsFourth() == null && result.getPointsFourth() != null) {
            this.setPointsFourth(result.getPointsFourth());
        }
        if (this.getPointsFifth() == null && result.getPointsFifth() != null) {
            this.setPointsFifth(result.getPointsFifth());
        }
        if (this.getPointsPenalty() == null && result.getPointsPenalty() != null) {
            this.setPointsPenalty(result.getPointsPenalty());
        }
    }

    public void setBand(BandDao band) {
        this.band = band;
    }

    public void setBandName(String name) {
        if (name == null) {
            this.bandName = null;
            return;
        }
        this.bandName = simplifyBandName(name);
    }

    public void setConductor(PersonDao person) {
        this.conductor = person;
        if (person != null && StringUtils.isBlank(this.originalConductorName)) {
            this.setOriginalConductorName(person.getName());
        }
    }
    public void setConductorSecond(PersonDao conductorTwo) {
        this.conductorSecond = conductorTwo;
    }
    public void setConductorThird(PersonDao conductorThree) {
        this.conductorThird = conductorThree;
    }

    public void setOriginalConductorName(String name) {
        this.originalConductorName = simplifyPersonFullName(name);
    }

    public void setOldId(String id) {
        this.oldId = id.strip();
    }

    public void setContestEvent(ContestEventDao contestEvent) {
        this.contestEvent = contestEvent;
    }

    public void setPosition(String position) {
        if (position == null) {
            this.position = null;
            this.resultPositionType = ResultPositionType.UNKNOWN;
            return;
        }

        switch (position.toUpperCase()) {
            case "W" -> {
                this.position = null;
                this.resultPositionType = ResultPositionType.WITHDRAWN;
            }
            case "D" -> {
                this.position = null;
                this.resultPositionType = ResultPositionType.DISQUALIFIED;
            }
            case "0", "", "NULL", "NONE" -> {
                this.position = null;
                this.resultPositionType = ResultPositionType.UNKNOWN;
            }
            default -> {
                this.position = Integer.parseInt(position);
                this.resultPositionType = ResultPositionType.RESULT;
            }
        }
    }

    public String getPositionDisplay() {
        String returnValue;
        switch(this.resultPositionType) {
            case WITHDRAWN -> returnValue = "W";
            case DISQUALIFIED -> returnValue = "D";
            case UNKNOWN -> returnValue = "";
            default -> returnValue = Integer.toString(this.position);
        }
        return returnValue;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public void setDrawSecond(Integer draw) {
        this.drawSecond = draw;
    }

    public void setDrawThird(Integer draw) {
        this.drawThird = draw;
    }

    public void setPointsTotal(String points){
        if (points == null) {
            this.pointsTotal = null;
        } else {
            if (points.strip().length() > 10) {
                points = points.strip().substring(0, 10);
            }
            this.pointsTotal = points.strip();
        }
    }
    public void setPointsFirst(String points){
        if (points == null) {
            this.pointsFirst = null;
        } else {
            this.pointsFirst = points.strip();
        }
    }
    public void setPointsSecond(String points) {
        if (points == null) {
            this.pointsSecond = null;
        } else {
            this.pointsSecond = points.strip();
        }
    }
    public void setPointsThird(String points){
        if (points == null) {
            this.pointsThird = null;
        } else {
            this.pointsThird = points.strip();
        }
    }
    public void setPointsFourth(String points){
        if (points == null) {
            this.pointsFourth = null;
        } else {
            this.pointsFourth = points.strip();
        }
    }
    public void setPointsFifth(String points){
        if (points == null) {
            this.pointsFifth = null;
        } else {
            this.pointsFifth = points.strip();
        }
    }
    public void setPointsPenalty(String points){
        if (points == null) {
            this.pointsPenalty = null;
        } else {
            this.pointsPenalty = points.strip();
        }
    }

    public void setNotes(String notes) {
        if (notes != null) {
            notes = notes.strip();
        }
        this.notes = notes;
    }

    public boolean getHasNotes() {
        return this.notes != null && this.notes.strip().length() > 0;
    }

    public String getCssClass() {
        String cssClass = "";
        if (this.resultPositionType == ResultPositionType.RESULT) {
            cssClass = "result-" + this.position;
        } else if (this.resultPositionType == ResultPositionType.WITHDRAWN) {
            cssClass = "table-light";
        }
        else if (this.resultPositionType == ResultPositionType.DISQUALIFIED) {
            cssClass = "table-secondary";
        }
        if (this.resultAward != null) {
            switch (this.resultAward) {
                case GOLD -> cssClass = "result-gold";
                case SILVER -> cssClass = "result-silver";
                case BRONZE -> cssClass = "result-bronze";
                case MERIT -> cssClass = "result-merit";
            }
        }

        return cssClass;
    }
}
