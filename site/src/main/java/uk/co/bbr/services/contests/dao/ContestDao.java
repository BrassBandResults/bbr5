package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
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
    @Setter
    private ContestGroupDao contestGroup;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="default_contest_type_id")
    @Setter
    private ContestTypeDao defaultContestType;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="region_id")
    @Setter
    private RegionDao region;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="section_id")
    @Setter
    private SectionDao section;

    @Column(name="ordering")
    @Setter
    private int ordering;

    @Column(name="description")
    private String description;

    @Column(name="notes")
    private String notes;

    @Column(name="extinct")
    @Setter
    private boolean extinct;

    @Column(name="exclude_from_group_results")
    @Setter
    private boolean excludeFromGroupResults;

    @Column(name="all_events_added")
    @Setter
    private boolean allEventsAdded;

    @Column(name="prevent_future_bands")
    @Setter
    private boolean preventFutureBands;

    @Column(name="repeat_period")
    @Setter
    private Integer repeatPeriod;

    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(
            name = "contest_tag_link",
            joinColumns = @JoinColumn(name = "contest_id"),
            inverseJoinColumns = @JoinColumn(name = "contest_tag_id"))
    private Set<ContestTagDao> tags = new HashSet<>();

    @Formula("(SELECT COUNT(*) FROM contest_event e WHERE e.contest_id = id)")
    private int eventsCount;

    public void setName(String name){
        String nameToSet = simplifyContestName(name);
        this.name = nameToSet;
    }

    public void setSlug(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.slug = value;
    }

    public void setNotes(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.notes = value;
    }

    public void setOldId(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.oldId = value;
    }

    public void setDescription(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.description = value;
    }
}
