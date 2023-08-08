package uk.co.bbr.services.tags.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

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

    @Transient
    @Setter
    private int groupCount;

    @Transient
    @Setter
    private int contestCount;

    public void setName(String name){
        this.name = simplifyContestName(name);
    }

    public void setOldId(String oldId){
        if (oldId == null) {
            this.oldId = null;
        } else {
            this.oldId = oldId.strip();
        }
    }

    public void setSlug(String slug){
        if (slug == null) {
            this.slug = null;
        } else {
            this.slug = slug.strip();
        }
    }
}
