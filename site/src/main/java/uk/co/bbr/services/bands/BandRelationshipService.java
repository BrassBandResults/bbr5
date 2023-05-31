package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BandRelationshipService {

    BandRelationshipTypeDao fetchIsParentOfRelationship();
    BandRelationshipDao saveRelationship(BandRelationshipDao relationship);
    BandRelationshipDao migrateRelationship(BandRelationshipDao relationship);
}
