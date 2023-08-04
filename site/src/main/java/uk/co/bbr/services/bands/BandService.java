package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandCompareDto;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.List;
import java.util.Optional;

public interface BandService {

    BandDao create(BandDao band);
    BandDao migrate(BandDao band);

    BandDao create(String bandName);

    BandDao create(String bandName, RegionDao region);

    BandListDto listBandsStartingWith(String prefix);

    Optional<BandDao> fetchBySlug(String bandSlug);

    Optional<BandDao> fetchBandByOldId(String bandOldId);

    BandDao update(BandDao band);

    List<BandWinnersSqlDto> fetchContestWinningBands();

    BandCompareDto compareBands(BandDao leftBand, BandDao rightBand);

    int countBandsCompetedInYear(int year);

    List<BandDao> findBandsWithMapLocationAndRehearsals(RegionDao region);

    List<BandDao> findBandsWithMapLocationAndRehearsals();

    void delete(BandDao band);
}
