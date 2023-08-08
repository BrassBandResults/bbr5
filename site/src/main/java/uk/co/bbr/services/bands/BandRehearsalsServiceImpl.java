package uk.co.bbr.services.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.repo.BandRehearsalDayRepository;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BandRehearsalsServiceImpl implements BandRehearsalsService, SlugTools {
    private final SecurityService securityService;
    private final BandRehearsalDayRepository bandRehearsalDayRepository;

    @Override
    @IsBbrMember
    public void createRehearsalDay(BandDao band, RehearsalDay day) {
        this.createRehearsalNight(band, day, null,false);
    }

    @Override
    public void createRehearsalDay(BandDao band, RehearsalDay day, String details) {
        this.createRehearsalNight(band, day, details,false);
    }

    private void createRehearsalNight(BandDao band, RehearsalDay day, String details, boolean migrating) {
        BandRehearsalDayDao rehearsalNight = new BandRehearsalDayDao();
        rehearsalNight.setBand(band);
        rehearsalNight.setDay(day);
        rehearsalNight.setDetails(details);

        if (!migrating) {
            rehearsalNight.setCreated(LocalDateTime.now());
            rehearsalNight.setCreatedBy(this.securityService.getCurrentUsername());
            rehearsalNight.setUpdated(LocalDateTime.now());
            rehearsalNight.setUpdatedBy(this.securityService.getCurrentUsername());
        } else {
            rehearsalNight.setCreated(band.getCreated());
            rehearsalNight.setCreatedBy(band.getCreatedBy());
            rehearsalNight.setUpdated(band.getUpdated());
            rehearsalNight.setUpdatedBy(band.getUpdatedBy());
        }

        this.bandRehearsalDayRepository.saveAndFlush(rehearsalNight);
    }

    @Override
    public List<RehearsalDay> findRehearsalDays(BandDao band) {
        List<BandRehearsalDayDao> rehearsalDays = this.bandRehearsalDayRepository.findForBand(band.getId());

        List<RehearsalDay> returnDays = new ArrayList<>();
        for (BandRehearsalDayDao bandDay : rehearsalDays) {
            if (bandDay.getBand().getId().equals(band.getId())) {
                returnDays.add(bandDay.getDay());
            }
        }
        return returnDays;
    }

    @Override
    public List<BandRehearsalDayDao> fetchRehearsalDays(BandDao band) {
        return this.bandRehearsalDayRepository.findForBand(band.getId());
    }

    @Override
    public void deleteRehearsalDays(BandDao band) {
        List<BandRehearsalDayDao> daysForBand = this.fetchRehearsalDays(band);
        this.bandRehearsalDayRepository.deleteAll(daysForBand);
    }
}
