package uk.co.bbr.services.groups.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import jakarta.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_group_alias")
public class ContestGroupAliasDao extends AbstractDao implements NameTools {
    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    @Setter
    private String oldId;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="contest_group_id")
    @Setter
    private ContestGroupDao contestGroup;

    public void setName(String value){
        this.name = simplifyContestName(value);
    }

    public String getDisplayName() {
        return this.name;
    }
}
