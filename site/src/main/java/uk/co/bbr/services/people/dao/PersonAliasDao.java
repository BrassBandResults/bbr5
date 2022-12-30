package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

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
@Table(name="person_alias")
public class PersonAliasDao extends AbstractDao implements NameTools {

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="person_id")
    private PersonDao person;

    @Column(name="name", nullable=false)
    private String oldName;

    @Column(name="hidden")
    private boolean hidden;

    public void setOldName(String name) {
        String nameToSet = simplifyName(name);
        this.oldName = nameToSet;
    }
}
