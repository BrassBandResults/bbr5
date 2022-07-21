package uk.co.bbr.services.band.dao;

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
@Table(name="BAND_RELATIONSHIP_TYPE")
public class BandRelationshipTypeDao extends AbstractDao {

    @Column(name="NAME", nullable=false)
    private String name;

    @Column(name="REVERSE_NAME", nullable=false)
    private String reverseName;
}
