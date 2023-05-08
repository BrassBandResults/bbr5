package uk.co.bbr.services.venues;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.repo.ContestEventRepository;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.services.venues.dto.VenueContestDto;
import uk.co.bbr.services.venues.dto.VenueContestYearDto;
import uk.co.bbr.services.venues.dto.VenueListDto;
import uk.co.bbr.services.venues.repo.VenueAliasRepository;
import uk.co.bbr.services.venues.repo.VenueRepository;
import uk.co.bbr.services.venues.sql.VenueSql;
import uk.co.bbr.services.venues.sql.dto.VenueListSqlDto;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService, SlugTools {

    private final VenueRepository venueRepository;
    private final VenueAliasRepository venueAliasRepository;
    private final ContestEventRepository contestEventRepository;
    private final SecurityService securityService;
    private final EntityManager entityManager;

    @Override
    @IsBbrMember
    public VenueDao create(String name) {
        VenueDao newVenue = new VenueDao();
        newVenue.setName(name);

        return this.create(newVenue);
    }

    @Override
    @IsBbrMember
    public VenueDao create(VenueDao venue) {
        return this.create(venue, false);
    }

    @Override
    @IsBbrAdmin
    public VenueDao migrate(VenueDao venue) {
        return this.create(venue, true);
    }

    private VenueDao create(VenueDao venue, boolean migrating) {
        // validation
        if (venue.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        this.validateMandatory(venue);

        // does the slug already exist?
        Optional<VenueDao> slugMatches = this.venueRepository.fetchBySlug(venue.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Venue with slug " + venue.getSlug() + " already exists.");
        }

        if (!migrating) {
            venue.setCreated(LocalDateTime.now());
            venue.setCreatedBy(this.securityService.getCurrentUsername());
            venue.setUpdated(LocalDateTime.now());
            venue.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.venueRepository.saveAndFlush(venue);
    }

    @Override
    public Optional<VenueAliasDao> aliasExists(VenueDao venue, String name) {
        return this.venueAliasRepository.findByVenueAndAliasName(venue.getId(), name);
    }

    @Override
    @IsBbrMember
    public VenueAliasDao createAlias(VenueDao venue, VenueAliasDao previousName) {
        return this.createAlias(venue, previousName, false);
    }

    @Override
    @IsBbrAdmin
    public VenueAliasDao migrateAlias(VenueDao venue, VenueAliasDao previousName) {
        return this.createAlias(venue, previousName, true);
    }

    private VenueAliasDao createAlias(VenueDao venue, VenueAliasDao previousName, boolean migrating) {
        previousName.setVenue(venue);

        if (!migrating) {
            previousName.setCreated(LocalDateTime.now());
            previousName.setCreatedBy(this.securityService.getCurrentUsername());
            previousName.setUpdated(LocalDateTime.now());
            previousName.setUpdatedBy(this.securityService.getCurrentUsername());
        }

        return this.venueAliasRepository.saveAndFlush(previousName);
    }

    @Override
    public Optional<VenueDao> fetchBySlug(String slug) {
        return this.venueRepository.fetchBySlug(slug);
    }

    private void validateMandatory(VenueDao venue){
        if (StringUtils.isBlank(venue.getName())) {
            throw new ValidationException("Venue name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(venue.getSlug())) {
            venue.setSlug(slugify(venue.getName()));
        }
    }


    @Override
    public VenueListDto listVenuesStartingWith(String prefix) {
        List<VenueListSqlDto> venuesToReturn;
        String prefixDisplay = prefix;

        switch (prefix.toUpperCase()) {
            case "ALL" -> venuesToReturn = VenueSql.venueListAll(this.entityManager);
            case "0" -> {
                prefixDisplay = "0-9";
                venuesToReturn = VenueSql.venueListNumber(this.entityManager);
            }
            default -> {
                if (prefix.trim().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                venuesToReturn = VenueSql.venueListPrefix(this.entityManager, prefix.trim().toUpperCase());
            }
        }

        long allVenuesCount = this.venueRepository.count();

        List<VenueDao> returnedVenues = new ArrayList<>();
        for (VenueListSqlDto eachVenue : venuesToReturn) {
            VenueDao venue = new VenueDao();
            venue.setName(eachVenue.getVenueName());
            venue.setSlug(eachVenue.getVenueSlug());
            venue.setEventCount(eachVenue.getEventCount());

            if (eachVenue.getRegionSlug() != null && eachVenue.getRegionSlug().length() > 0) {
                RegionDao region = new RegionDao();
                region.setSlug(eachVenue.getRegionSlug());
                region.setName(eachVenue.getRegionName());
                region.setCountryCode(eachVenue.getCountryCode());
                venue.setRegion(region);
            }

            returnedVenues.add(venue);
        }
        return new VenueListDto(venuesToReturn.size(), allVenuesCount, prefixDisplay, returnedVenues);
    }

    @Override
    public List<VenueAliasDao> fetchAliases(VenueDao venue) {
        return this.venueAliasRepository.findByVenue(venue.getId());
    }

    @Override
    @IsBbrMember
    public VenueDao update(VenueDao venue) {
        return this.venueRepository.saveAndFlush(venue);
    }

    private List<ContestEventDao> contestEventsForVenue(VenueDao venue) {
        return this.contestEventRepository.fetchEventsForVenue(venue.getId());
    }

    @Override
    public List<VenueContestDto> fetchVenueContests(VenueDao venue) {
        List<VenueContestDto> returnList = new ArrayList<>();

        List<ContestEventDao> eventsForVenue = this.contestEventsForVenue(venue);
        for (ContestEventDao eachEvent : eventsForVenue) {
            boolean found = false;
            for (VenueContestDto eachReturnResult : returnList) {
                if (eachReturnResult.getContest().getSlug().equals(eachEvent.getContest().getSlug())) {
                    eachReturnResult.incrementEventCount();
                    found = true;
                }
            }
            if (!found) {
                VenueContestDto newReturnResult = new VenueContestDto(eachEvent.getContest());
                returnList.add(newReturnResult);
            }
        }
        return returnList;
    }

    @Override
    public List<ContestResultDao> fetchVenueContestEvents(VenueDao venue, ContestDao contest) {
        return null;
    }

    @Override
    public List<VenueContestYearDto> fetchVenueContestYears(VenueDao venue) {
        List<VenueContestYearDto> returnList = new ArrayList<>();

        List<ContestEventDao> eventsForVenue = this.contestEventsForVenue(venue);
        for (ContestEventDao eachEvent : eventsForVenue) {
            boolean found = false;
            for (VenueContestYearDto eachReturnResult : returnList) {
                if (eachReturnResult.getYear() == eachEvent.getEventDate().getYear()) {
                    eachReturnResult.incrementEventCount();
                    found = true;
                }
            }
            if (!found) {
                VenueContestYearDto newReturnResult = new VenueContestYearDto(eachEvent.getEventDate().getYear());
                returnList.add(newReturnResult);
            }
        }
        return returnList.stream().sorted(Comparator.comparing(VenueContestYearDto::getYear).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<ContestEventDao> fetchVenueContestYear(VenueDao venue, int year) {
        return this.contestEventRepository.fetchEventsForVenueInYear(venue.getId(), year);
    }
}
