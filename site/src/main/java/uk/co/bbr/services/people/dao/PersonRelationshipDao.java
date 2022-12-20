package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

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
@Table(name="person_relationship")
public class PersonRelationshipDao extends AbstractDao {

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="left_person_id")
    private PersonDao leftPerson;

    @Column(name="left_person_name")
    private String leftPersonName;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="relationship_id")
    private PersonRelationshipTypeDao relationship;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="right_person_id")
    private PersonDao rightPerson;

    @Column(name="right_person_name")
    private String rightPersonName;
}
