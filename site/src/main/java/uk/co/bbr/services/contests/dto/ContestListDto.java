package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContestListDto {
    private final String searchPrefix;
    private final List<ContestListContestDto> returnedContests;
}
