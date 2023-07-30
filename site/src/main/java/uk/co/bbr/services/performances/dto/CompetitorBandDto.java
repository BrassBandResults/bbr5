package uk.co.bbr.services.performances.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CompetitorBandDto {
    private final String bandName;
    private final String bandPosition;
    private final List<CompetitorDto> competitors;

    public boolean hasCompetitors() {
        return this.competitors != null && !this.competitors.isEmpty();
    }
}
