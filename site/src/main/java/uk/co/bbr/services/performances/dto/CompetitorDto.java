package uk.co.bbr.services.performances.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompetitorDto {
    private final String usercode;
    private final String position;
    private final boolean privateUser;
}
