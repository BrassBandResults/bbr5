package uk.co.bbr.services.events.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SpecialAwardDto {
    private final String year;
    private final String translationKey;
}
