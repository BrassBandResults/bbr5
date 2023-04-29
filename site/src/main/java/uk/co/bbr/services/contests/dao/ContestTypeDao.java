package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_type")
public class ContestTypeDao extends AbstractDao implements NameTools {
    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="draw_one_title")
    private String drawOneTitle;

    @Column(name="draw_two_title")
    private String drawTwoTitle;

    @Column(name="draw_three_title")
    private String drawThreeTitle;

    @Column(name="points_total_title")
    private String pointsTotalTitle;

    @Column(name="points_one_title")
    private String pointsOneTitle;

    @Column(name="points_two_title")
    private String pointsTwoTitle;

    @Column(name="points_three_title")
    private String pointsThreeTitle;

    @Column(name="points_four_title")
    private String pointsFourTitle;

    @Column(name="points_penalty_title")
    private String pointsPenaltyTitle;

    @Column(name="has_test_piece", nullable=false)
    private boolean testPiece;

    @Column(name="has_own_choice", nullable=false)
    private boolean ownChoice;

    @Column(name="has_entertainments", nullable=false)
    private boolean entertainments;

    @Column(name="statistics_show", nullable=false)
    private boolean statistics;

    @Column(name="statistics_limit")
    private int statisticsLimit;

    public void setName(String name){
        String nameToSet = simplifyContestName(name);
        this.name = nameToSet;
    }
}
