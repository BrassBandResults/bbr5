package uk.co.bbr.services.years;

import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.years.sql.dto.YearListEntrySqlDto;

import java.util.List;

public interface YearService {
    List<YearListEntrySqlDto> fetchFullYearList();

    List<ContestResultDao> fetchEventsForYear(String year);
}
