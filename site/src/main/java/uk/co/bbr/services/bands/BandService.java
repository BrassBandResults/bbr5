package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.List;
import java.util.Optional;

public interface BandService {

    BandDao create(BandDao band);
    BandDao migrate(BandDao band);

    BandDao create(String bandName);

    BandDao create(String bandName, RegionDao region);

    BandListDto listBandsStartingWith(String prefix);

    void createRehearsalNight(BandDao band, RehearsalDay day);
    void migrateRehearsalNight(BandDao band, RehearsalDay day);

    List<RehearsalDay> findRehearsalNights(BandDao band);

    Optional<BandDao> fetchBandBySlug(String bandSlug);

    Optional<BandDao> fetchBandByOldId(String bandOldId);

    BandPreviousNameDao createPreviousName(BandDao band, BandPreviousNameDao previousName);
    BandPreviousNameDao migratePreviousName(BandDao band, BandPreviousNameDao previousName);

    BandRelationshipTypeDao fetchIsParentOfRelationship();

    BandRelationshipDao saveRelationship(BandRelationshipDao relationship);
    BandRelationshipDao migrateRelationship(BandRelationshipDao relationship);

    BandDao update(BandDao band);

    Optional<BandPreviousNameDao> aliasExists(BandDao band, String aliasName);








}
