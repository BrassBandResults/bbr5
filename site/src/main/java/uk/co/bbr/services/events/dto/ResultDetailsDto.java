package uk.co.bbr.services.events.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.events.dao.ContestResultDao;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ResultDetailsDto {
    private final List<ContestResultDao> bandNonWhitResults;
    private final List<ContestResultDao> bandWhitResults;
    private final List<ContestResultDao> bandAllResults;
}
