package uk.co.bbr.services.performances;

import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.performances.dto.CompetitorBandDto;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.BestOwnChoiceDto;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.sql.dto.PiecesPerSectionSqlDto;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.dao.SiteUserDao;

import java.util.List;
import java.util.Optional;

public interface PerformanceService {

    List<PerformanceDao> fetchPendingPerformancesForUser(SiteUserDao user);
    List<PerformanceDao> fetchApprovedPerformancesForUser(SiteUserDao user);

    void linkUserPerformance(SiteUserDao user, ContestResultDao contestResult);

    List<CompetitorBandDto> fetchPerformancesForEvent(ContestEventDao contestEvent);

    List<ContestResultDao> fetchApprovedPerformancesForPiece(String userCode, PieceDao piece);

    List<PerformanceDao> fetchPerformancesForResult(ContestResultDao contestResult);

    Optional<PerformanceDao> fetchPerformance(SiteUserDao currentUser, Long performanceId);

    void update(PerformanceDao performance);

    void delete(PerformanceDao performance);
}
