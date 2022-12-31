package uk.co.bbr.services.contests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.repo.ContestRepository;
import uk.co.bbr.services.contests.types.ContestGroupType;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService, SlugTools {

    private final SecurityService securityService;
    private final ContestTypeService contestTypeService;

    private final ContestRepository contestRepository;


    @Override
    @IsBbrMember
    public ContestDao create(String name) {
        ContestDao contest = new ContestDao();
        contest.setName(name);

        return this.create(contest);
    }

    @Override
    @IsBbrMember
    public ContestDao create(ContestDao contest) {
        // validation
        if (contest.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (StringUtils.isBlank(contest.getName())) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(contest.getSlug())) {
            contest.setSlug(slugify(contest.getName()));
        }

        if (contest.getDefaultContestType() == null) {
            contest.setDefaultContestType(this.contestTypeService.fetchDefaultContestType());
        }

        // does the slug already exist?
        Optional<ContestGroupDao> slugMatches = this.contestRepository.findBySlug(contest.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Contest with slug " + contest.getSlug() + " already exists.");
        }

        // does the slug already exist?
        Optional<ContestGroupDao> nameMatches = this.contestRepository.findByName(contest.getName());
        if (nameMatches.isPresent()) {
            throw new ValidationException("Contest with name " + contest.getName() + " already exists.");
        }

        contest.setCreated(LocalDateTime.now());
        contest.setCreatedBy(this.securityService.getCurrentUserId());

        return this.contestRepository.saveAndFlush(contest);
    }
}
