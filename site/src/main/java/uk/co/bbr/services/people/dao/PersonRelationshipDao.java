package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="person_relationship")
public class PersonRelationshipDao extends AbstractDao {

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="left_person_id")
    @Setter
    private PersonDao leftPerson;

    @Column(name="left_person_name")
    private String leftPersonName;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="relationship_id")
    @Setter
    private PersonRelationshipTypeDao relationship;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="right_person_id")
    @Setter
    private PersonDao rightPerson;

    @Column(name="right_person_name")
    private String rightPersonName;

    public String relationshipName(PersonDao person) {
        if (this.leftPerson.getId().equals(person.getId())) {
            return this.relationship.getName();
        } else {
            return this.relationship.getReverseName();
        }
    }

    public PersonDao otherPerson(PersonDao person) {
        if (this.leftPerson.getId().equals(person.getId())) {
            return this.rightPerson;
        } else {
            return this.leftPerson;
        }
    }

}
