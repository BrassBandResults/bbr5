package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ContestStreakDto {
    private final String bandName;
    private final String bandSlug;
    private final List<String> years;
}
