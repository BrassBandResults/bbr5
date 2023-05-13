package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestTagDao;
import uk.co.bbr.services.framework.types.EntityType;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContestTagDetailsDto {
    private final ContestTagDao tag;
    private final List<ContestDao> contests;
    private final List<ContestGroupDao> contestGroups;

    public List<ContestTagDetailsContestDto> getSortedList() {
        List<ContestTagDetailsContestDto> returnList = new ArrayList<>();

        for (ContestDao contest : this.contests) {
            returnList.add(new ContestTagDetailsContestDto(contest.getName(), contest.getSlug(), EntityType.CONTEST));
        }

        for (ContestGroupDao contestGroup : this.contestGroups) {
            returnList.add(new ContestTagDetailsContestDto(contestGroup.getName(), contestGroup.getSlug(), EntityType.GROUP));
        }

        return returnList;
    }
}
