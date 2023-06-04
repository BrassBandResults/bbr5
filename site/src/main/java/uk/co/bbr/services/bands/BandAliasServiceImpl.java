package uk.co.bbr.services.bands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.repo.BandPreviousNameRepository;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BandAliasServiceImpl implements BandAliasService, SlugTools {
    private final SecurityService securityService;
    private final BandPreviousNameRepository bandPreviousNameRepository;

    @Override
    public Optional<BandAliasDao> aliasExists(BandDao band, String aliasName) {
        String name = band.simplifyBandName(aliasName);
        return this.bandPreviousNameRepository.fetchByNameForBand(band.getId(), name);
    }

    @Override
    public List<BandAliasDao> findVisibleAliases(BandDao band) {
        return this.bandPreviousNameRepository.findVisibleForBandOrderByName(band.getId());
    }

    @Override
    public List<BandAliasDao> findAllAliases(BandDao band) {
        return this.bandPreviousNameRepository.findAllForBandOrderByName(band.getId());
    }


    @Override
    @IsBbrMember
    public BandAliasDao createAlias(BandDao band, BandAliasDao previousName) {
        return createPreviousName(band, previousName, false);
    }

    @Override
    public BandAliasDao createAlias(BandDao band, String name) {
        BandAliasDao newAlias = new BandAliasDao();
        newAlias.setOldName(name);
        return this.createAlias(band, newAlias);
    }

    @Override
    @IsBbrAdmin
    public BandAliasDao migrateAlias(BandDao band, BandAliasDao previousName) {
        return createPreviousName(band, previousName, true);
    }

    private BandAliasDao createPreviousName(BandDao band, BandAliasDao previousName, boolean migrating) {
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
    public void showAlias(BandDao band, Long aliasId) {
        Optional<BandAliasDao> previousName = this.bandPreviousNameRepository.fetchByIdForBand(band.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(band.getSlug(), aliasId);
        }
        previousName.get().setHidden(false);
        this.bandPreviousNameRepository.saveAndFlush(previousName.get());
    }

    @Override
    public void hideAlias(BandDao band, Long aliasId) {
        Optional<BandAliasDao> previousName = this.bandPreviousNameRepository.fetchByIdForBand(band.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(band.getSlug(), aliasId);
        }
        previousName.get().setHidden(true);
        this.bandPreviousNameRepository.saveAndFlush(previousName.get());
    }

    @Override
    public void deleteAlias(BandDao band, Long aliasId) {
        Optional<BandAliasDao> previousName = this.bandPreviousNameRepository.fetchByIdForBand(band.getId(), aliasId);
        if (previousName.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(band.getSlug(), aliasId);
        }
        this.bandPreviousNameRepository.delete(previousName.get());

    }

    @Override
    public Optional<BandAliasDao> fetchAliasByBandAndId(BandDao band, Long aliasId) {
        return this.bandPreviousNameRepository.fetchByIdForBand(band.getId(), aliasId);
    }

    @Override
    public void updateAlias(BandDao band, BandAliasDao previousName) {
        if (previousName.getStartDate() != null && previousName.getEndDate() != null && previousName.getStartDate().isAfter(previousName.getEndDate())) {
            throw new ValidationException("Start date can't be after end date");
        }

        Optional<BandAliasDao> existingAliasOptional = this.bandPreviousNameRepository.fetchByIdForBand(band.getId(), previousName.getId());
        if (existingAliasOptional.isEmpty()) {
            throw NotFoundException.bandAliasNotFoundByIds(band.getSlug(), previousName.getId());
        }
        BandAliasDao existingAlias = existingAliasOptional.get();

        existingAlias.setStartDate(previousName.getStartDate());
        existingAlias.setEndDate(previousName.getEndDate());

        existingAlias.setUpdated(LocalDateTime.now());
        existingAlias.setUpdatedBy(this.securityService.getCurrentUsername());

        this.bandPreviousNameRepository.saveAndFlush(existingAlias);
    }
}
