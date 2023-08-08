package uk.co.bbr.services.bands;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRehearsalDayDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dto.BandCompareDto;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.repo.BandPreviousNameRepository;
import uk.co.bbr.services.bands.repo.BandRehearsalDayRepository;
import uk.co.bbr.services.bands.repo.BandRelationshipRepository;
import uk.co.bbr.services.bands.repo.BandRepository;
import uk.co.bbr.services.bands.sql.BandCompareSql;
import uk.co.bbr.services.bands.sql.BandMapSql;
import uk.co.bbr.services.bands.sql.BandSql;
import uk.co.bbr.services.bands.sql.dto.BandListSqlDto;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.bands.sql.dto.CompareBandsSqlDto;
import uk.co.bbr.services.bands.sql.dto.RegionBandSqlDto;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BandServiceImpl implements BandService, SlugTools {
    private final RegionService regionService;
    private final SecurityService securityService;

    private final BandRepository bandRepository;
    private final BandRehearsalDayRepository bandRehearsalDayRepository;
    private final BandRelationshipRepository bandRelationshipRepository;
    private final BandPreviousNameRepository bandPreviousNameRepository;

    private final EntityManager entityManager;

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

        Optional<BandDao> existingBand = this.bandRepository.fetchById(band.getId());
        if (existingBand.isEmpty()) {
            throw new UnsupportedOperationException("Can't find existing band to update");
        }

        band.setUpdated(LocalDateTime.now());
        band.setUpdatedBy(this.securityService.getCurrentUsername());
        return this.bandRepository.saveAndFlush(band);
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
        List<BandListSqlDto> bandsToReturn;

        switch (prefix.toUpperCase()) {
            case "ALL" -> bandsToReturn = BandSql.selectAllBandsForList(this.entityManager);
            case "0" -> bandsToReturn = BandSql.selectBandsStartingWithNumbersForList(this.entityManager);
            default -> {
                if (prefix.strip().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                bandsToReturn = BandSql.selectBandsStartingWithLetterForList(this.entityManager, prefix.strip().toUpperCase());
            }
        }

        long allBandsCount = this.bandRepository.count();
        List<BandDao> bands = new ArrayList<>();
        for (BandListSqlDto band : bandsToReturn) {
            bands.add(band.asBand());
        }
        return new BandListDto(bandsToReturn.size(), allBandsCount, prefix, bands);
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
    public List<BandWinnersSqlDto> fetchContestWinningBands() {
        return BandSql.selectWinningBands(this.entityManager);
    }

    @Override
    public BandCompareDto compareBands(BandDao leftBand, BandDao rightBand) {
        List<CompareBandsSqlDto> results = BandCompareSql.compareBands(this.entityManager, leftBand.getId(), rightBand.getId());
        return new BandCompareDto(results);
    }

    @Override
    public int countBandsCompetedInYear(int year) {
        return BandSql.countBandsCompetedInYear(this.entityManager, year);
    }

    @Override
    public List<BandDao> findBandsWithMapLocationAndRehearsals(RegionDao region) {
        List<BandDao> bandData = new ArrayList<>();
        List<RegionBandSqlDto> sqlData = BandMapSql.selectBandsForRegionMap(this.entityManager, region.getId());
        for (RegionBandSqlDto eachRow : sqlData) {
            bandData.add(eachRow.getBand());
        }
        return bandData;
    }

    @Override
    public List<BandDao> findBandsWithMapLocationAndRehearsals() {
        List<BandDao> bandData = new ArrayList<>();
        List<RegionBandSqlDto> sqlData = BandMapSql.selectBandsWithRehearsalsForBandMap(this.entityManager);
        for (RegionBandSqlDto eachRow : sqlData) {
            BandDao band = eachRow.getBand();
            if (band.getRehearsalsBinary() == null || Integer.parseInt(band.getRehearsalsBinary(), 2) == 0) {
                continue;
            }
            bandData.add(eachRow.getBand());
        }
        return bandData;
    }

    @Override
    public void delete(BandDao band) {
        List<BandRehearsalDayDao> days = this.bandRehearsalDayRepository.findForBand(band.getId());
        this.bandRehearsalDayRepository.deleteAll(days);

        List<BandRelationshipDao> relationships = this.bandRelationshipRepository.findForBand(band.getId());
        this.bandRelationshipRepository.deleteAll(relationships);

        List<BandAliasDao> aliases = this.bandPreviousNameRepository.findAllForBandOrderByName(band.getId());
        this.bandPreviousNameRepository.deleteAll(aliases);

        this.bandRepository.delete(band);
    }
}
