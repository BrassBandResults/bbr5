package uk.co.bbr.services.lookup;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.dao.ContestResultPieceDao;
import uk.co.bbr.services.events.dto.ContestEventSummaryDto;
import uk.co.bbr.services.events.dto.GroupListDto;
import uk.co.bbr.services.events.dto.GroupListGroupDto;
import uk.co.bbr.services.events.repo.ContestEventRepository;
import uk.co.bbr.services.events.repo.ContestResultPieceRepository;
import uk.co.bbr.services.framework.NotFoundException;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.groups.dao.ContestGroupAliasDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.groups.dto.ContestGroupDetailsDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearsDetailsDto;
import uk.co.bbr.services.groups.dto.ContestGroupYearsDetailsYearDto;
import uk.co.bbr.services.groups.repo.ContestGroupAliasRepository;
import uk.co.bbr.services.groups.repo.ContestGroupRepository;
import uk.co.bbr.services.groups.types.ContestGroupType;
import uk.co.bbr.services.lookup.sql.LookupSql;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LookupServiceImpl implements LookupService, SlugTools {

    private final EntityManager entityManager;

    @Override
    public List<LookupSqlDto> lookupPeople(String searchString) {
        return LookupSql.lookupPeople(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupGroups(String searchString) {
        return LookupSql.lookupGroups(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupVenues(String searchString) {
        return LookupSql.lookupVenues(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupContests(String searchString) {
        return LookupSql.lookupContests(this.entityManager, searchString);
    }

    @Override
    public List<LookupSqlDto> lookupBands(String searchString) {
        return LookupSql.lookupBands(this.entityManager, searchString);
    }
}
