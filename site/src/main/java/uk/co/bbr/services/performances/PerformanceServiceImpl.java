package uk.co.bbr.services.performances;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.performances.dto.CompetitorBandDto;
import uk.co.bbr.services.performances.dto.CompetitorDto;
import uk.co.bbr.services.performances.repo.PerformanceRepository;
import uk.co.bbr.services.performances.sql.PerformancesSql;
import uk.co.bbr.services.performances.sql.dto.PiecePerformanceSqlDto;
import uk.co.bbr.services.performances.types.PerformanceStatus;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.types.ContestHistoryVisibility;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService, SlugTools {

    private final SecurityService securityService;
    private final UserService userService;
    private final ResultService resultService;

    private final EntityManager entityManager;
    private final PerformanceRepository performanceRepository;

    @Override
    public List<PerformanceDao> fetchPendingPerformancesForUser(SiteUserDao user) {
        return this.performanceRepository.fetchPendingUserPerformances(user.getUsercode());
    }

    @Override
    public List<PerformanceDao> fetchApprovedPerformancesForUser(SiteUserDao user) {
        return this.performanceRepository.fetchApprovedUserPerformances(user.getUsercode());
    }

    @Override
    public void linkUserPerformance(SiteUserDao user, ContestResultDao contestResult) {
        PerformanceDao newPerformance = new PerformanceDao();
        newPerformance.setCreated(LocalDateTime.now());
        newPerformance.setCreatedBy(user.getUsercode());
        newPerformance.setUpdated(LocalDateTime.now());
        newPerformance.setUpdatedBy(this.securityService.getCurrentUsername());
        newPerformance.setResult(contestResult);
        newPerformance.setStatus(PerformanceStatus.ACCEPTED);
        newPerformance.setInstrument(null);

        this.performanceRepository.saveAndFlush(newPerformance);
    }

    @Override
    public List<CompetitorBandDto> fetchPerformancesForEvent(ContestEventDao contestEvent) {
        List<CompetitorBandDto> returnList = new ArrayList<>();

        List<PerformanceDao> competitors = this.performanceRepository.fetchForEvent(contestEvent.getId());
        List<ContestResultDao> results = this.resultService.fetchForEvent(contestEvent);
        for (ContestResultDao result : results) {
            if (result.getResultPositionType().equals(ResultPositionType.DISQUALIFIED) || result.getResultPositionType().equals(ResultPositionType.WITHDRAWN)) {
                continue;
            }

            List<CompetitorDto> competitorsForThisResult = new ArrayList<>();
            for (PerformanceDao eachCompetitor : competitors) {
                if (eachCompetitor.getResult().getId().equals(result.getId())) {
                    Optional<SiteUserDao> user = this.userService.fetchUserByUsercode(eachCompetitor.getCreatedBy());
                    boolean historyPrivate = ContestHistoryVisibility.PRIVATE.equals(user.get().getContestHistoryVisibility());
                    String instrument = null;
                    if (eachCompetitor.getInstrument() != null) {
                        instrument = eachCompetitor.getInstrument().getTranslationKey();
                    }
                    CompetitorDto competitorToAdd = new CompetitorDto(user.get().getUsercode(), instrument, historyPrivate);
                    competitorsForThisResult.add(competitorToAdd);
                }
            }
            CompetitorBandDto band = new CompetitorBandDto(result.getBandName(), result.getPositionDisplay(), competitorsForThisResult);
            returnList.add(band);
        }

        return returnList;
    }

    @Override
    public List<ContestResultDao> fetchApprovedPerformancesForPiece(String userCode, PieceDao piece) {
        List<PiecePerformanceSqlDto> performances = PerformancesSql.selectPerformancesOfPiece(this.entityManager, userCode, piece.getId());

        List<ContestResultDao> returnList = new ArrayList<>();
        for (PiecePerformanceSqlDto performance : performances) {
            returnList.add(performance.asResult());
        }

        return returnList;
    }

    @Override
    public List<PerformanceDao> fetchPerformancesForResult(ContestResultDao contestResult) {
        return this.performanceRepository.fetchPerformancesForResult(contestResult.getId());
    }
}
