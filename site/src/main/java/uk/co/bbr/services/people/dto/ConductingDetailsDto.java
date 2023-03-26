package uk.co.bbr.services.people.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.contests.dao.ContestResultDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ConductingDetailsDto {
    private final List<ContestResultDao> bandResults;
    private final List<ContestResultDao> bandWhitResults;
}
