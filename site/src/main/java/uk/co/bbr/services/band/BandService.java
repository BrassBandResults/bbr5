package uk.co.bbr.services.band;

import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.dto.BandListDto;
import uk.co.bbr.services.region.dao.RegionDao;

public interface BandService {

    BandDao create(BandDao band);

    BandDao create(String bandName);

    BandDao create(String bandName, RegionDao region);

    BandListDto listBandsStartingWith(String prefix);
}
