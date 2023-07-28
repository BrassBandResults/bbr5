package uk.co.bbr.services.events.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.groups.dao.ContestGroupDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GroupListDto {
    private final int returnedGroupsCount;
    private final long allGroupsCount;
    private final String searchPrefix;
    private final List<ContestGroupDao> returnedGroups;
}
