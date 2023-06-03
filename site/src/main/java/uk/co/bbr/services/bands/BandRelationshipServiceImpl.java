package uk.co.bbr.services.bands;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.bands.repo.BandRelationshipRepository;
import uk.co.bbr.services.bands.repo.BandRelationshipTypeRepository;
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
    public BandRelationshipDao createRelationship(BandRelationshipDao relationship) {
        if (relationship.getStartDate() != null && relationship.getEndDate() != null && relationship.getStartDate().isAfter(relationship.getEndDate())) {
            throw new ValidationException("Start date can't be after end date");
        }
        if (relationship.getId() != null) {
            throw new ValidationException("ID must not be supplied to create");
        }

        return this.createRelationship(relationship, false);
    }

    @Override
    public BandRelationshipDao updateRelationship(BandRelationshipDao relationship) {
        if (relationship.getId() == null) {
            throw new ValidationException("ID required to update");
        }

        relationship.setUpdated(LocalDateTime.now());
        relationship.setUpdatedBy(this.securityService.getCurrentUsername());

        return this.saveRelationship(relationship);
    }

    @Override
    @IsBbrAdmin
    public BandRelationshipDao migrateRelationship(BandRelationshipDao relationship) {
        return this.createRelationship(relationship, true);
    }

    private BandRelationshipDao createRelationship(BandRelationshipDao relationship, boolean migrating) {
        if (!migrating) {
            relationship.setCreated(LocalDateTime.now());
            relationship.setCreatedBy(this.securityService.getCurrentUsername());
            relationship.setUpdated(LocalDateTime.now());
            relationship.setUpdatedBy(this.securityService.getCurrentUsername());
        }

        return this.saveRelationship(relationship);
    }

    private BandRelationshipDao saveRelationship(BandRelationshipDao relationship){
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

        return this.bandRelationshipRepository.saveAndFlush(relationship);
    }

    @Override
    public List<BandRelationshipDao> fetchRelationshipsForBand(BandDao band) {
        return this.bandRelationshipRepository.findForBand(band.getId());
    }

    @Override
    public Optional<BandRelationshipDao> fetchById(Long relationshipId) {
        return this.bandRelationshipRepository.fetchById(relationshipId);
    }

    @Override
    public void deleteRelationship(BandRelationshipDao bandRelationship) {
        this.bandRelationshipRepository.delete(bandRelationship);
    }

    @Override
    public Optional<BandRelationshipTypeDao> fetchTypeById(long relationshipTypeId) {
        return this.bandRelationshipTypeRepository.findById(relationshipTypeId);
    }
}
