package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GroupListDto {
    private final int returnedGroupsCount;
    private final long allGroupsCount;
    private final String searchPrefix;
    private final List<GroupListGroupDto> returnedGroups;
}
