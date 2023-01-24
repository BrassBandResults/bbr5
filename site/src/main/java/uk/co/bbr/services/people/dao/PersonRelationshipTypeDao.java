package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="person_relationship_type")
public class PersonRelationshipTypeDao extends AbstractDao {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="reverse_name", nullable=false)
    private String reverseName;
}
