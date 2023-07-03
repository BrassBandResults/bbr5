package uk.co.bbr.services.statistics;

import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.statistics.dto.StatisticsDto;

import java.util.Optional;

public interface StatisticsService {
    StatisticsDto fetchStatistics();
}
