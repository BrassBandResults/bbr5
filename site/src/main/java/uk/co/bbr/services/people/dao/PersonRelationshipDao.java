package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="PERSON_RELATIONSHIP")
public class PersonRelationshipDao extends AbstractDao {

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="LEFT_PERSON_ID")
    private PersonDao leftPerson;

    @Column(name="LEFT_PERSON_NAME")
    private String leftPersonName;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="RELATIONSHIP_ID")
    private PersonRelationshipTypeDao relationship;

    @ManyToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="RIGHT_PERSON_ID")
    private PersonDao rightPerson;

    @Column(name="RIGHT_PERSON_NAME")
    private String rightPersonName;
}
