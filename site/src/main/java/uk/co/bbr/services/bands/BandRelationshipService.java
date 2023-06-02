package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;

import java.util.List;
import java.util.Optional;

public interface BandRelationshipService {

    BandRelationshipTypeDao fetchIsParentOfRelationship();
    BandRelationshipDao createRelationship(BandRelationshipDao relationship);
    BandRelationshipDao updateRelationship(BandRelationshipDao relationship);
    BandRelationshipDao migrateRelationship(BandRelationshipDao relationship);
    List<BandRelationshipDao> fetchRelationshipsForBand(BandDao band);
    Optional<BandRelationshipDao> fetchById(Long relationshipId);
    void deleteRelationship(BandRelationshipDao bandRelationship);
}
