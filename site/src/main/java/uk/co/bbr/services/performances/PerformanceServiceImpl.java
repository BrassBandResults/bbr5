package uk.co.bbr.services.performances;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.events.types.ResultAwardType;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.performances.dao.PerformanceDao;
import uk.co.bbr.services.performances.repo.PerformanceRepository;
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.BestOwnChoiceDto;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.repo.PieceAliasRepository;
import uk.co.bbr.services.pieces.repo.PieceRepository;
import uk.co.bbr.services.pieces.sql.PieceSql;
import uk.co.bbr.services.pieces.sql.dto.BestPieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.OwnChoiceUsagePieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.PieceUsageCountSqlDto;
import uk.co.bbr.services.pieces.sql.dto.PiecesPerSectionSqlDto;
import uk.co.bbr.services.pieces.sql.dto.SetTestUsagePieceSqlDto;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService, SlugTools {

    private final PerformanceRepository performanceRepository;

    @Override
    public List<PerformanceDao> fetchPendingPerformancesForUser(SiteUserDao user) {
        return this.performanceRepository.fetchPendingUserPerformances(user.getUsercode());
    }

    @Override
    public List<PerformanceDao> fetchApprovedPerformancesForUser(SiteUserDao user) {
        return this.performanceRepository.fetchApprovedUserPerformances(user.getUsercode());
    }
}
