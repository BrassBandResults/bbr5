package uk.co.bbr.services.contests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.framework.types.EntityType;

@Getter
@RequiredArgsConstructor
public class ContestTagDetailsContestDto {
    private final String name;
    private final String slug;
    private final EntityType type;
}
