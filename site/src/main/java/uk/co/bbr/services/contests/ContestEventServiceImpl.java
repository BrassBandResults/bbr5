package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.repo.ContestEventRepository;
import uk.co.bbr.services.contests.repo.ContestGroupRepository;
import uk.co.bbr.services.contests.types.ContestEventDateResolution;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestEventServiceImpl implements ContestEventService {

    private final SecurityService securityService;
    private final ContestEventRepository contestEventRepository;

    @Override
    @IsBbrMember
    public ContestEventDao create(ContestDao contest, LocalDate eventDate) {
        ContestEventDao contestEvent = new ContestEventDao();
        contestEvent.setEventDate(eventDate);

        return this.create(contest, contestEvent);
    }

    @Override
    @IsBbrMember
    public ContestEventDao create(ContestDao contest, ContestEventDao event) {
        event.setContest(contest);

        // default in contest type if not specified
        if (event.getContestType() == null) {
            event.setContestType(contest.getDefaultContestType());
        }

        // default in date resolution if not specified
        if (event.getDateResolution() == null) {
            event.setDateResolution(ContestEventDateResolution.EXACT_DATE);
        }

        // default in name if not specified
        if (event.getName() == null) {
            event.setName(contest.getName());
        }

        if (event.getOriginalOwner() == null) {
            event.setOriginalOwner(this.securityService.getCurrentUsername());
        }

        return this.contestEventRepository.saveAndFlush(event);
    }


}
