package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.types.RehearsalDay;

import java.util.List;

public interface BandRehearsalsService {

    void createRehearsalDay(BandDao band, RehearsalDay day);
    void createRehearsalDay(BandDao band, RehearsalDay day, String details);

    List<RehearsalDay> findRehearsalDays(BandDao band);

    List<BandRehearsalDayDao> fetchRehearsalDays(BandDao band);

    void deleteRehearsalDays(BandDao band);
}
