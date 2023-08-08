package uk.co.bbr.services.tags;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.tags.dto.ContestTagDetailsDto;
import uk.co.bbr.services.tags.repo.ContestTagRepository;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.tags.sql.ContestTagSql;
import uk.co.bbr.services.tags.sql.dto.TagListSqlDto;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestTagServiceImpl implements ContestTagService, SlugTools {

    private final ContestTagRepository contestTagRepository;
    private final SecurityService securityService;
    private final EntityManager entityManager;

    @Override
    @IsBbrMember
    public ContestTagDao create(String name) {
        ContestTagDao newTag = new ContestTagDao();
        newTag.setName(name);

        return this.create(newTag);
    }

    @Override
    @IsBbrMember
    public ContestTagDao migrate(ContestTagDao contestTag) {
        return this.create(contestTag, true);
    }

    @Override
    public Optional<ContestTagDao> fetchByName(String name) {
        return this.contestTagRepository.fetchByName(name);
    }

    @Override
    public List<ContestTagDao> listTagsStartingWith(String prefix) {
        List<TagListSqlDto> tagsSql;
        if (prefix.equalsIgnoreCase("ALL")) {
            tagsSql = ContestTagSql.selectAllTagsWithCounts(this.entityManager);
        } else {
            if (prefix.strip().length() != 1) {
                throw new UnsupportedOperationException("Prefix must be a single character");
            }
            tagsSql = ContestTagSql.selectTagsForPrefixWithCount(this.entityManager, prefix.strip().toUpperCase());
        }

        List<ContestTagDao> tagsToReturn = new ArrayList<>();
        for (TagListSqlDto eachTag : tagsSql) {
            tagsToReturn.add(eachTag.asContestTag());
        }

        return tagsToReturn;
    }

    @Override
    public ContestTagDetailsDto fetchDetailsBySlug(String slug) {
        Optional<ContestTagDao> tag = this.fetchBySlug(slug);
        if (tag.isEmpty()) {
            throw NotFoundException.tagNotFoundBySlug(slug);
        }

        List<ContestDao> contests = this.contestTagRepository.fetchContestsForTag(slug);
        List<ContestGroupDao> contestGroups = this.contestTagRepository.fetchGroupsForTag(slug);

        return new ContestTagDetailsDto(tag.get(), contests, contestGroups);
    }

    @Override
    public Optional<ContestTagDao> fetchBySlug(String slug) {
        return this.contestTagRepository.fetchBySlug(slug);
    }

    @Override
    public void deleteTag(ContestTagDao tag) {
        int tagLinks = tag.getContestCount() + tag.getGroupCount();
        if (tagLinks > 0) {
            throw new ValidationException("Can't delete tag that has links to contests or groups");
        }

        this.contestTagRepository.delete(tag);
    }

    @Override
    @IsBbrMember
    public ContestTagDao create(ContestTagDao contestTag) {
        return this.create(contestTag, false);
    }

    private ContestTagDao create(ContestTagDao contestTag, boolean migrating) {
        // validation
        if (contestTag.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        if (StringUtils.isBlank(contestTag.getName())) {
            throw new ValidationException("Band name must be specified");
        }

        // defaults
        if (StringUtils.isBlank(contestTag.getSlug())) {
            contestTag.setSlug(slugify(contestTag.getName()));
        }

        // does the slug already exist?
        Optional<ContestTagDao> slugMatches = this.contestTagRepository.fetchBySlug(contestTag.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Contest Tag with slug " + contestTag.getSlug() + " already exists.");
        }

        // does the slug already exist?
        Optional<ContestTagDao> nameMatches = this.contestTagRepository.fetchByName(contestTag.getName());
        if (nameMatches.isPresent()) {
            throw new ValidationException("Contest Tag with name " + contestTag.getName() + " already exists.");
        }

        if (!migrating) {
            contestTag.setCreated(LocalDateTime.now());
            contestTag.setCreatedBy(this.securityService.getCurrentUsername());
            contestTag.setUpdated(LocalDateTime.now());
            contestTag.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.contestTagRepository.saveAndFlush(contestTag);
    }
}
