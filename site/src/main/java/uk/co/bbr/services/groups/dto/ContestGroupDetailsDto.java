package uk.co.bbr.services.groups.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContestGroupDetailsDto {
    private final ContestGroupDao contestGroup;
    private final List<ContestDao> activeContests;
    private final List<ContestDao> oldContests;
}
