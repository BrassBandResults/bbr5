package uk.co.bbr.services.contests.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import javax.persistence.*;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_tag")
public class ContestTagDao extends AbstractDao implements NameTools {
    @Column(name="name", nullable=false)
    private String name;

    @Column(name="old_id")
    private String oldId;

    @Column(name="slug", nullable=false)
    private String slug;

    public void setName(String name){
        String nameToSet = simplifyContestName(name);
        this.name = nameToSet;
    }

    public void setOldId(String oldId){
        if (oldId == null) {
            this.oldId = null;
        } else {
            this.oldId = oldId.trim();
        }
    }

    public void setSlug(String slug){
        if (slug == null) {
            this.slug = null;
        } else {
            this.slug = slug.trim();
        }
    }
}
