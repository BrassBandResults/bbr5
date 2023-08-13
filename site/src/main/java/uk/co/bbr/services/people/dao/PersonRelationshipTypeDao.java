package uk.co.bbr.services.people.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.bbr.services.framework.AbstractDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
