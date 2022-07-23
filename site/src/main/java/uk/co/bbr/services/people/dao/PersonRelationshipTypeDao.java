package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="PERSON_RELATIONSHIP_TYPE")
public class PersonRelationshipTypeDao extends AbstractDao {

    @Column(name="NAME", nullable=false)
    private String name;

    @Column(name="REVERSE_NAME", nullable=false)
    private String reverseName;
}
