package uk.co.bbr.services.venues.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="venue_alternative_name")
public class VenueAlternativeNameDao extends AbstractDao implements NameTools {
}
