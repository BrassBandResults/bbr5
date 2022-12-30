package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.services.people.dao.PersonDao;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="contest_group")
public class ContestGroupDao extends AbstractDao implements NameTools {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="group_type", nullable=false)
    private ContestGroupType groupType;

    @Column(name="notes")
    private String notes;

    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(
            name = "contest_group_tag_link",
            joinColumns = @JoinColumn(name = "contest_group_id"),
            inverseJoinColumns = @JoinColumn(name = "contest_tag_id"))
    private Set<ContestTagDao> tags = new HashSet<>();

    public void setName(String name){
        String nameToSet = simplifyName(name);
        this.name = nameToSet;
    }
}
