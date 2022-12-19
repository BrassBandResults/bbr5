package uk.co.bbr.services.band;

import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.dao.BandPreviousNameDao;
import uk.co.bbr.services.band.dao.BandRelationshipDao;
import uk.co.bbr.services.band.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.band.dto.BandListDto;
import uk.co.bbr.services.band.types.RehearsalDay;
import uk.co.bbr.services.region.dao.RegionDao;

import java.util.List;

public interface BandService {

    BandDao create(BandDao band);

    BandDao create(String bandName);

    BandDao create(String bandName, RegionDao region);

    BandListDto listBandsStartingWith(String prefix);

    void createRehearsalNight(BandDao band, RehearsalDay day);

    List<RehearsalDay> fetchRehearsalNights(BandDao band);

    BandDao findBandBySlug(String bandSlug);

    BandDao fetchBandByOldId(Long bandOldId);

    void createPreviousName(BandDao band, BandPreviousNameDao previousName);

    BandRelationshipTypeDao fetchIsParentOfRelationship();

    void saveRelationship(BandRelationshipDao relationship);

    void update(BandDao band);
}
