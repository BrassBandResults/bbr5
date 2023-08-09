package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="person_alias")
public class PersonAliasDao extends AbstractDao implements NameTools {

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="person_id")
    @Setter
    private PersonDao person;

    @Column(name="name", nullable=false)
    private String oldName;

    @Column(name="hidden")
    @Setter
    private boolean hidden;

    public void setOldName(String name) {
        this.oldName = simplifyPersonFullName(name);
    }

    public boolean matchesName(String personName) {
        return personName != null && personName.equalsIgnoreCase(this.oldName);
    }

    public String getDisplayName() {
        return this.oldName;
    }
}
