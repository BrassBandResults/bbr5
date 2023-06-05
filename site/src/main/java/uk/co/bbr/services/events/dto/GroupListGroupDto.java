package uk.co.bbr.services.events.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupListGroupDto {
    private final String slug;
    private final String name;
    private final int contestCount;
}
