package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.bands.dto.BandCompareDto;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.bands.types.ResultSetCategory;
import uk.co.bbr.services.people.dto.ConductorCompareDto;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.time.LocalDate;
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
}
