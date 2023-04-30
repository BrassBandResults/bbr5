package uk.co.bbr.services.bands;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
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
        return this.create(band, false);
    }

    @Override
    @IsBbrAdmin
    public BandDao migrate(BandDao band) {
        return this.create(band, true);
    }

    private BandDao create(BandDao band, boolean migrating) {
        // validation
        if (band.getId() != null) {
            throw new ValidationException("Can't create band with specific id");
        }

       this.validateMandatory(band);

        // does the slug already exist?
        Optional<BandDao> slugMatches = this.bandRepository.fetchBySlug(band.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Band with slug " + band.getSlug() + " already exists.");
        }

        if (!migrating) {
            band.setCreated(LocalDateTime.now());
            band.setCreatedBy(this.securityService.getCurrentUsername());
            band.setUpdated(LocalDateTime.now());
            band.setUpdatedBy(this.securityService.getCurrentUsername());
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
    @IsBbrMember
    public BandDao update(BandDao band) {
        if (band.getId() == null) {
            throw new UnsupportedOperationException("Can't update without an id");
        }

        this.validateMandatory(band);

        band.setUpdated(LocalDateTime.now());
        band.setUpdatedBy(this.securityService.getCurrentUsername());
        return this.bandRepository.saveAndFlush(band);
    }

    @Override
    public Optional<BandPreviousNameDao> aliasExists(BandDao band, String aliasName) {
        String name = band.simplifyBandName(aliasName);
        return this.bandPreviousNameRepository.fetchByNameForBand(band.getId(), name);
    }

    @Override
    public List<BandPreviousNameDao> findVisiblePreviousNames(BandDao band) {
        return this.bandPreviousNameRepository.findVisibleForBandOrderByName(band.getId());
    }

    @Override
    public BandDao findMatchingBandByName(String searchBandName, LocalDate dateContext) {
        String bandName = searchBandName.toUpperCase().trim();
        String bandNameLessBand = null;
        if (bandName.toLowerCase().endsWith("band")) {
            bandNameLessBand = bandName.substring(0, bandName.length() - "band".length()).trim();
        }
        String bandNameLessBrassBand = null;
        if (bandName.toLowerCase().endsWith("brassband")) {
            bandNameLessBrassBand = bandName.substring(0, bandName.length() - "brassband".length()).trim();
        }

        List<BandDao> bandMatches = new ArrayList<>();


        bandMatches = this.bandRepository.findExactNameMatch(bandName);

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandPreviousNameRepository.findAliasExactNameMatch(bandName);
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandRepository.findExactNameMatch(bandName + " Band");
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandPreviousNameRepository.findAliasExactNameMatch(bandName + " Band");
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandRepository.findContainsNameMatch("%" + bandName + "% ");
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandPreviousNameRepository.findContainsNameMatch("%" + bandName + "% ");
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandRepository.findContainsNameMatch("%" + bandName + "%") ;
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandPreviousNameRepository.findContainsNameMatch("%" + bandName + "%");
        }

        if (bandNameLessBand != null) {
            if (bandMatches.isEmpty()) {
                bandMatches = this.bandRepository.findContainsNameMatch("%" + bandNameLessBand + "%");
            }

            if (bandMatches.isEmpty()) {
                bandMatches = this.bandPreviousNameRepository.findContainsNameMatch("%" + bandNameLessBand + "%");
            }
        }

        if (bandNameLessBrassBand != null) {
            if (bandMatches.isEmpty()) {
                bandMatches = this.bandRepository.findContainsNameMatch("%" + bandNameLessBrassBand + "%");
            }

            if (bandMatches.isEmpty()) {
                bandMatches = this.bandPreviousNameRepository.findContainsNameMatch("%" + bandNameLessBrassBand + "%");
            }
        }

        List<BandDao> returnList = new ArrayList<>();

        // remove any matches that fall outside the date context
        for (BandDao match : bandMatches) {
            if (match.getStartDate() != null) {
                if (match.getStartDate().isAfter(dateContext)) {
                    continue;
                }
            }

            if (match.getEndDate() != null) {
                if (match.getEndDate().isBefore(dateContext)) {
                    continue;
                }
            }

            returnList.add(match);
        }

        if (returnList.isEmpty()) {
            return null;
        }

        return returnList.get(0);
    }

    private void validateMandatory(BandDao band){
        if (StringUtils.isBlank(band.getName())) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(band.getSlug())) {
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
            case "0" -> bandsToReturn = this.bandRepository.findWithNumberPrefixOrderByName();
            default -> {
                if (prefix.trim().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                bandsToReturn = this.bandRepository.findByPrefixOrderByName(prefix.trim().toUpperCase());
            }
        }

        long allBandsCount = this.bandRepository.count();

        List<BandListBandDto> returnedBands = new ArrayList<>();
        for (BandDao eachBand : bandsToReturn) {
            returnedBands.add(new BandListBandDto(eachBand.getSlug(), eachBand.getName(), eachBand.getRegion(),eachBand.getResultsCount(), eachBand.getDateRange()));
        }
        return new BandListDto(bandsToReturn.size(), allBandsCount, prefix, returnedBands);
    }


    @Override
    @IsBbrMember
    public void createRehearsalDay(BandDao band, RehearsalDay day) {
        this.createRehearsalNight(band, day, false);
    }

    @Override
    @IsBbrAdmin
    public void migrateRehearsalNight(BandDao band, RehearsalDay day) {
        this.createRehearsalNight(band, day, true);
    }

    private void createRehearsalNight(BandDao band, RehearsalDay day, boolean migrating) {
        BandRehearsalDayDao rehearsalNight = new BandRehearsalDayDao();
        rehearsalNight.setBand(band);
        rehearsalNight.setDay(day);

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

        this.bandRehearsalNightRepository.saveAndFlush(rehearsalNight);
    }

    @Override
    public List<RehearsalDay> findRehearsalNights(BandDao band) {
        List<BandRehearsalDayDao> rehearsalDays = this.bandRehearsalNightRepository.findForBand(band.getId());

        List<RehearsalDay> returnDays = new ArrayList<>();
        for (BandRehearsalDayDao bandDay : rehearsalDays) {
            if (bandDay.getBand().getId().equals(band.getId())) {
                returnDays.add(bandDay.getDay());
            }
        }
        return returnDays;
    }

    @Override
    public Optional<BandDao> fetchBySlug(String bandSlug) {
        return this.bandRepository.fetchBySlug(bandSlug);
    }

    @Override
    public Optional<BandDao> fetchBandByOldId(String bandOldId) {
        return this.bandRepository.fetchByOldId(bandOldId);
    }

    @Override
    @IsBbrMember
    public BandPreviousNameDao createPreviousName(BandDao band, BandPreviousNameDao previousName) {
        return createPreviousName(band, previousName, false);
    }

    @Override
    @IsBbrAdmin
    public BandPreviousNameDao migratePreviousName(BandDao band, BandPreviousNameDao previousName) {
        return createPreviousName(band, previousName, true);
    }

    private BandPreviousNameDao createPreviousName(BandDao band, BandPreviousNameDao previousName, boolean migrating) {
        previousName.setBand(band);

        if (previousName.getStartDate() != null && previousName.getEndDate() != null && previousName.getStartDate().isAfter(previousName.getEndDate())) {
            throw new ValidationException("Start date can't be after end date");
        }

        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUsername());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.bandPreviousNameRepository.saveAndFlush(previousName);
    }

    @Override
    public BandRelationshipTypeDao fetchIsParentOfRelationship() {
        return this.bandRelationshipTypeRepository.fetchIsParentOfRelationship();
    }

    @Override
    @IsBbrMember
    public BandRelationshipDao saveRelationship(BandRelationshipDao relationship) {
        if (relationship.getStartDate() != null && relationship.getEndDate() != null && relationship.getStartDate().isAfter(relationship.getEndDate())) {
            throw new ValidationException("Start date can't be after end date");
        }

        return this.saveRelationship(relationship, false);
    }

    @Override
    @IsBbrAdmin
    public BandRelationshipDao migrateRelationship(BandRelationshipDao relationship) {
        return this.saveRelationship(relationship, true);
    }

    private BandRelationshipDao saveRelationship(BandRelationshipDao relationship, boolean migrating) {
        if (relationship.getLeftBand() != null) {
            if (StringUtils.isBlank(relationship.getLeftBandName())) {
                relationship.setLeftBandName(relationship.getLeftBand().getName());
            }
        }

        if (relationship.getRightBand() != null) {
            if (StringUtils.isBlank(relationship.getRightBandName())) {
                relationship.setRightBandName(relationship.getRightBand().getName());
            }
        }

        if (!migrating) {
            relationship.setCreated(LocalDateTime.now());
            relationship.setCreatedBy(this.securityService.getCurrentUsername());
            relationship.setUpdated(LocalDateTime.now());
            relationship.setUpdatedBy(this.securityService.getCurrentUsername());
        }

        return this.bandRelationshipRepository.saveAndFlush(relationship);
    }
}
