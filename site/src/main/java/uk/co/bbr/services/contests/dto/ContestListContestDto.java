package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContestListContestDto {
    private final String slug;
    private final String name;
    private final int contestResultsCount;
}
