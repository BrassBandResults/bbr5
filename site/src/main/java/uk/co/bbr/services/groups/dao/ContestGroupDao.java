package uk.co.bbr.services.groups.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.groups.types.ContestGroupType;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;
import uk.co.bbr.web.HtmlTools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
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
    @Setter
    private ContestGroupType groupType;

    @Column(name="notes")
    private String notes;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
            name = "contest_group_tag_link",
            joinColumns = @JoinColumn(name = "contest_group_id"),
            inverseJoinColumns = @JoinColumn(name = "contest_tag_id"))
    private Set<ContestTagDao> tags = new HashSet<>();

    @Formula("(SELECT COUNT(*) FROM contest c WHERE c.contest_group_id = id)")
    private int contestCount;

    @Formula("(SELECT COUNT(*) FROM contest_event e INNER JOIN contest c ON e.contest_id = c.id WHERE c.contest_group_id = id)")
    private int eventsCount;

    public void setName(String name){
        this.name = simplifyContestName(name);
    }

    public void setSlug(String value) {
        if (value != null) {
            this.slug = value.trim().toUpperCase();
        } else {
            this.slug = null;
        }
    }

    public String getSlug() {
        if (this.slug == null) {
            return null;
        }
        return this.slug.toUpperCase();
    }

    public void setOldId(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.oldId = value;
    }

    public void setNotes(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.notes = value;
    }

    public boolean hasNotes() {
        return !StringUtils.isBlank(this.notes);
    }

    public ObjectNode asLookup(ObjectMapper objectMapper) {
        ObjectNode group = objectMapper.createObjectNode();
        group.put("slug", this.getSlug());
        group.put("name", this.escapeJson(this.name));
        group.put("context", "");
        return group;
    }
}
