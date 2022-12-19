package uk.co.bbr.services.band;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.dao.BandPreviousNameDao;
import uk.co.bbr.services.band.dao.BandRehearsalDayDao;
import uk.co.bbr.services.band.dao.BandRelationshipDao;
import uk.co.bbr.services.band.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.band.dto.BandListBandDto;
import uk.co.bbr.services.band.dto.BandListDto;
import uk.co.bbr.services.band.repo.BandPreviousNameRepository;
import uk.co.bbr.services.band.repo.BandRehearsalNightRepository;
import uk.co.bbr.services.band.repo.BandRelationshipRepository;
import uk.co.bbr.services.band.repo.BandRelationshipTypeRepository;
import uk.co.bbr.services.band.repo.BandRepository;
import uk.co.bbr.services.band.types.BandStatus;
import uk.co.bbr.services.band.types.RehearsalDay;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BandServiceImpl implements BandService, SlugTools {
    private final RegionService regionService;
    private final BandRepository bandRepository;
    private final SecurityService securityService;
    private final BandPreviousNameRepository bandPreviousNameRepository;
    private final BandRehearsalNightRepository bandRehearsalNightRepository;
    private final BandRelationshipRepository bandRelationshipRepository;
    private final BandRelationshipTypeRepository bandRelationshipTypeRepository;

    @Override
    @IsBbrMember
    public BandDao create(BandDao band) {
        // validation
        if (band.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (band.getName() == null || band.getName().trim().length() == 0) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (band.getSlug() == null || band.getSlug().trim().length() == 0) {
            band.setSlug(slugify(band.getName()));
        }

        if (band.getRegion() == null) {
            RegionDao unknownRegion = this.regionService.fetchUnknownRegion();
            band.setRegion(unknownRegion);
        }

        if (band.getStatus() == null) {
            band.setStatus(BandStatus.COMPETING);
        }

        return this.bandRepository.saveAndFlush(band);
    }

    @Override
    @IsBbrMember
    public BandDao create(String bandName) {
        BandDao newBand = new BandDao();
        newBand.setName(bandName);
        return this.create(newBand);
    }

    @Override
    @IsBbrMember
    public BandDao create(String bandName, RegionDao region) {
        BandDao newBand = new BandDao();
        newBand.setName(bandName);
        newBand.setRegion(region);
        return this.create(newBand);
    }

    @Override
    public BandListDto listBandsStartingWith(String prefix) {
        List<BandDao> bandsToReturn;

        switch (prefix.toUpperCase()) {
            case "ALL" -> bandsToReturn = this.bandRepository.findAll();
            case "0" -> bandsToReturn = this.bandRepository.findWithNumberPrefix();
            default -> {
                if (prefix.trim().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                bandsToReturn = this.bandRepository.findByPrefix(prefix.trim().toUpperCase());
            }
        }

        long allBandsCount = this.bandRepository.count();

        List<BandListBandDto> returnedBands = new ArrayList<>();
        for (BandDao eachBand : bandsToReturn) {
            returnedBands.add(new BandListBandDto(eachBand.getSlug(), eachBand.getName(), eachBand.getRegion().getName(), eachBand.getRegion().getSlug(), eachBand.getRegion().getCountryCode(), 0, eachBand.getDateRange()));
        }
        return new BandListDto(bandsToReturn.size(), allBandsCount, prefix, returnedBands);
    }

    @Override
    public void createRehearsalNight(BandDao band, RehearsalDay day) {
        BandRehearsalDayDao rehearsalNight = new BandRehearsalDayDao();
        rehearsalNight.setBand(band);
        rehearsalNight.setDay(day);
        this.bandRehearsalNightRepository.saveAndFlush(rehearsalNight);
    }

    @Override
    public List<RehearsalDay> fetchRehearsalNights(BandDao band) {
        List<BandRehearsalDayDao> rehearsalDays = this.bandRehearsalNightRepository.fetchForBand(band.getId());

        List<RehearsalDay> returnDays = new ArrayList<>();
        for (BandRehearsalDayDao bandDay : rehearsalDays) {
            returnDays.add(bandDay.getDay());
        }
        return returnDays;
    }

    @Override
    public BandDao findBandBySlug(String bandSlug) {
        Optional<BandDao> band = this.bandRepository.findBySlug(bandSlug);
        if (band.isEmpty()) {
            throw new NotFoundException("Band with slug " + bandSlug + " not found");
        }
        return band.get();
    }

    @Override
    public BandDao fetchBandByOldId(Long bandOldId) {
        Optional<BandDao> band = this.bandRepository.fetchByOldId(Long.toString(bandOldId));
        if (band.isEmpty()) {
            throw new NotFoundException("Band with old id " + bandOldId + " not found");
        }
        return band.get();
    }

    @Override
    public void createPreviousName(BandDao band, BandPreviousNameDao previousName) {
        previousName.setBand(band);
        this.bandPreviousNameRepository.saveAndFlush(previousName);
    }

    @Override
    public BandRelationshipTypeDao fetchIsParentOfRelationship() {
        return this.bandRelationshipTypeRepository.fetchIsParentOfRelationship();
    }

    @Override
    public void saveRelationship(BandRelationshipDao relationship) {
        this.bandRelationshipRepository.saveAndFlush(relationship);
    }

    @Override
    public void update(BandDao band) {
        band.setUpdated(LocalDateTime.now());
        band.setUpdatedBy(this.securityService.getCurrentUser().getId());
        this.bandRepository.saveAndFlush(band);
    }
}
