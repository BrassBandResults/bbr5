package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.types.ResultPositionType;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.people.dao.PersonDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="contest_result")
public class ContestResultDao extends AbstractDao implements NameTools {
    @Column(name="old_id")
    private String oldId;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_event_id")
    private ContestEventDao contestEvent;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="band_id")
    private BandDao band;

    @Column(name="band_name")
    private String bandName;

    @Column(name="result_position_type")
    private ResultPositionType resultPositionType;

    @Column(name="result_position")
    private Integer result;

    @Column(name="draw")
    private Integer draw;

    @Column(name="draw_second")
    private Integer drawSecond;

    @Column(name="draw_third")
    private Integer drawThird;

    @Column(name="points_total")
    private String points;

    @Column(name="points_first")
    private String pointsFirst;

    @Column(name="points_second")
    private String pointsSecond;

    @Column(name="points_third")
    private String pointsThird;

    @Column(name="points_forth")
    private String pointsFourth;

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
    private String conductorName;

    @Column(name="notes")
    private String notes;
}
