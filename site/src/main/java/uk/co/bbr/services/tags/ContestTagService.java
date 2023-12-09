package uk.co.bbr.services.tags;

import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.tags.dto.ContestTagDetailsDto;

import java.util.List;
import java.util.Optional;

public interface ContestTagService {
    ContestTagDao create(String name);
    ContestTagDao create(ContestTagDao contestTag);
    ContestTagDao migrate(ContestTagDao contestTag);

    Optional<ContestTagDao> fetchByName(String name);

    List<ContestTagDao> listTagsStartingWith(String prefix);

    List<ContestTagDao> listUnusedTags();

    ContestTagDetailsDto fetchDetailsBySlug(String slug);

    Optional<ContestTagDao> fetchBySlug(String tagSlug);

    void deleteTag(ContestTagDao tag);
}
