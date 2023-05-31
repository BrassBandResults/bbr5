package uk.co.bbr.services.bands;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.bands.dto.BandListBandDto;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.repo.BandPreviousNameRepository;
import uk.co.bbr.services.bands.repo.BandRehearsalNightRepository;
import uk.co.bbr.services.bands.repo.BandRelationshipRepository;
import uk.co.bbr.services.bands.repo.BandRelationshipTypeRepository;
import uk.co.bbr.services.bands.repo.BandRepository;
import uk.co.bbr.services.bands.sql.BandSql;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BandRelationshipServiceImpl implements BandRelationshipService, SlugTools {

    private final SecurityService securityService;

    private final BandRelationshipRepository bandRelationshipRepository;
    private final BandRelationshipTypeRepository bandRelationshipTypeRepository;



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
