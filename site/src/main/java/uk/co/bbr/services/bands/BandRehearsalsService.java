package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.types.RehearsalDay;

import java.util.List;

public interface BandRehearsalsService {

    void createRehearsalDay(BandDao band, RehearsalDay day);
    void createRehearsalDay(BandDao band, RehearsalDay day, String details);
    void migrateRehearsalDay(BandDao band, RehearsalDay day);

    List<RehearsalDay> findRehearsalDays(BandDao band);

    List<BandRehearsalDayDao> fetchRehearsalDays(BandDao band);

    void deleteRehearsalDays(BandDao band);

    List<BandRehearsalDayDao> fetchBandsByDay(RehearsalDay day);
}
