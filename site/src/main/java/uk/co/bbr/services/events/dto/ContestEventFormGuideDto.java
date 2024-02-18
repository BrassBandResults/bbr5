package uk.co.bbr.services.events.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.events.dao.ContestResultDao;

import java.util.List;

@Getter
@Setter
public class ContestEventFormGuideDto {
    private ContestResultDao result;
    private List<ContestResultDao> thisContest;
    private List<ContestResultDao> otherContests;
}
