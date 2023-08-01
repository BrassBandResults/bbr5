package uk.co.bbr.services.bands.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.types.RehearsalDay;

import java.util.List;

public interface BandRehearsalNightRepository extends JpaRepository<BandRehearsalDayDao, Long> {
    @Query("SELECT rd FROM BandRehearsalDayDao rd WHERE rd.band.id = ?1 ORDER BY rd.day")
    List<BandRehearsalDayDao> findForBand(Long bandId);

    @Query("SELECT COUNT(rd) FROM BandRehearsalDayDao rd WHERE rd.day = uk.co.bbr.services.bands.types.RehearsalDay.MONDAY")
    int countBandsOnMonday();

    @Query("SELECT COUNT(rd) FROM BandRehearsalDayDao rd WHERE rd.day = uk.co.bbr.services.bands.types.RehearsalDay.TUESDAY")
    int countBandsOnTuesday();

    @Query("SELECT COUNT(rd) FROM BandRehearsalDayDao rd WHERE rd.day = uk.co.bbr.services.bands.types.RehearsalDay.WEDNESDAY")
    int countBandsOnWednesday();

    @Query("SELECT COUNT(rd) FROM BandRehearsalDayDao rd WHERE rd.day = uk.co.bbr.services.bands.types.RehearsalDay.THURSDAY")
    int countBandsOnThursday();

    @Query("SELECT COUNT(rd) FROM BandRehearsalDayDao rd WHERE rd.day = uk.co.bbr.services.bands.types.RehearsalDay.FRIDAY")
    int countBandsOnFriday();

    @Query("SELECT COUNT(rd) FROM BandRehearsalDayDao rd WHERE rd.day = uk.co.bbr.services.bands.types.RehearsalDay.SATURDAY")
    int countBandsOnSaturday();

    @Query("SELECT COUNT(rd) FROM BandRehearsalDayDao rd WHERE rd.day = uk.co.bbr.services.bands.types.RehearsalDay.SUNDAY")
    int countBandsOnSunday();

    @Query("SELECT COUNT(DISTINCT rd.band) FROM BandRehearsalDayDao rd")
    int fetchBandCount();

    @Query("SELECT rd FROM BandRehearsalDayDao rd WHERE rd.day = :day AND LEN(rd.band.latitude) > 0 AND LEN (rd.band.longitude) > 0")
    List<BandRehearsalDayDao> findForDay(RehearsalDay day);
}
