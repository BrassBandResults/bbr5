package uk.co.bbr.services.bands.dao;

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
@Table(name="band_relationship_type")
public class BandRelationshipTypeDao extends AbstractDao {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="reverse_name", nullable=false)
    private String reverseName;
}
