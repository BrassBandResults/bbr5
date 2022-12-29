package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.List;

public interface BandService {

    BandDao create(BandDao band);

    BandDao create(String bandName);

    BandDao create(String bandName, RegionDao region);

    BandListDto listBandsStartingWith(String prefix);

    void createRehearsalNight(BandDao band, RehearsalDay day);

    List<RehearsalDay> fetchRehearsalNights(BandDao band);

    BandDao findBandBySlug(String bandSlug);

    BandDao fetchBandByOldId(String bandOldId);

    BandPreviousNameDao createPreviousName(BandDao band, BandPreviousNameDao previousName);

    BandRelationshipTypeDao fetchIsParentOfRelationship();

    BandRelationshipDao saveRelationship(BandRelationshipDao relationship);

    BandDao update(BandDao band);
}
