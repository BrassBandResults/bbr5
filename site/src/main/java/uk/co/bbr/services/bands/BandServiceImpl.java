package uk.co.bbr.services.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.bands.dto.BandListBandDto;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.repo.BandPreviousNameRepository;
import uk.co.bbr.services.bands.repo.BandRehearsalNightRepository;
import uk.co.bbr.services.bands.repo.BandRelationshipRepository;
import uk.co.bbr.services.bands.repo.BandRelationshipTypeRepository;
import uk.co.bbr.services.bands.repo.BandRepository;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
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
            throw new ValidationException("Can't create band with specific id");
        }

       this.validateMandatory(band);

        // does the slug already exist?
        Optional<BandDao> slugMatches = this.bandRepository.findBySlug(band.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Band with slug " + band.getSlug() + " already exists.");
        }

        band.setCreated(LocalDateTime.now());
        band.setCreatedBy(this.securityService.getCurrentUserId());
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
    @IsBbrMember
    public BandDao update(BandDao band) {
        if (band.getId() == null) {
            throw new UnsupportedOperationException("Can't update without an id");
        }

        this.validateMandatory(band);

        band.setUpdated(LocalDateTime.now());
        band.setUpdatedBy(this.securityService.getCurrentUserId());
        return this.bandRepository.saveAndFlush(band);
    }

    private void validateMandatory(BandDao band){
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
    @IsBbrMember
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
    public BandDao fetchBandByOldId(String bandOldId) {
        Optional<BandDao> band = this.bandRepository.fetchByOldId(bandOldId);
        if (band.isEmpty()) {
            throw new NotFoundException("Band with old id " + bandOldId + " not found");
        }
        return band.get();
    }

    @Override
    @IsBbrMember
    public BandPreviousNameDao createPreviousName(BandDao band, BandPreviousNameDao previousName) {
        previousName.setBand(band);
        return this.bandPreviousNameRepository.saveAndFlush(previousName);
    }

    @Override
    public BandRelationshipTypeDao fetchIsParentOfRelationship() {
        return this.bandRelationshipTypeRepository.fetchIsParentOfRelationship();
    }

    @Override
    @IsBbrMember
    public BandRelationshipDao saveRelationship(BandRelationshipDao relationship) {
        if (relationship.getLeftBand() != null) {
            if (relationship.getLeftBandName() == null || relationship.getLeftBandName().trim().length() == 0) {
                relationship.setLeftBandName(relationship.getLeftBand().getName());
            }
        }

        if (relationship.getRightBand() != null) {
            if (relationship.getRightBandName() == null || relationship.getRightBandName().trim().length() == 0) {
                relationship.setRightBandName(relationship.getRightBand().getName());
            }
        }

        return this.bandRelationshipRepository.saveAndFlush(relationship);
    }
}
