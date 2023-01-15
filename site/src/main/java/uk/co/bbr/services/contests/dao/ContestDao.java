package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="contest")
public class ContestDao extends AbstractDao implements NameTools {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="slug", nullable=false)
    private String slug;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="contest_group_id")
    private ContestGroupDao contestGroup;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="default_contest_type_id")
    private ContestTypeDao defaultContestType;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="region_id")
    private RegionDao region;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="section_id")
    private SectionDao section;

    @Column(name="ordering")
    private int ordering;

    @Column(name="description")
    private String description;

    @Column(name="notes")
    private String notes;

    @Column(name="extinct")
    private boolean extinct;

    @Column(name="exclude_from_group_results")
    private boolean excludeFromGroupResults;

    @Column(name="all_events_added")
    private boolean allEventsAdded;

    @Column(name="prevent_future_bands")
    private boolean preventFutureBands;

    @Column(name="repeat_period")
    private Integer repeatPeriod;

    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(
            name = "contest_tag_link",
            joinColumns = @JoinColumn(name = "contest_id"),
            inverseJoinColumns = @JoinColumn(name = "contest_tag_id"))
    private Set<ContestTagDao> tags = new HashSet<>();

    public void setName(String name){
        String nameToSet = simplifyName(name);
        this.name = nameToSet;
    }
}
