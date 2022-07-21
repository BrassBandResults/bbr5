package uk.co.bbr.services.band;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.repo.BandPreviousNameRepository;
import uk.co.bbr.services.band.repo.BandRehearsalNightRepository;
import uk.co.bbr.services.band.repo.BandRelationshipRepository;
import uk.co.bbr.services.band.repo.BandRelationshipTypeRepository;
import uk.co.bbr.services.band.repo.BandRepository;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.SlugTools;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.web.security.annotations.IsBbrMember;

@Service
@RequiredArgsConstructor
public class BandServiceImpl implements BandService, SlugTools {
    private final RegionService regionService;

    private final BandRepository bandRepository;
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

        return this.bandRepository.save(band);
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
}
