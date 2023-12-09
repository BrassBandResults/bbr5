package uk.co.bbr.services.pieces;

import jakarta.persistence.EntityManager;
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
import uk.co.bbr.services.pieces.dao.PieceAliasDao;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.pieces.dto.BestOwnChoiceDto;
import uk.co.bbr.services.pieces.dto.PieceListDto;
import uk.co.bbr.services.pieces.repo.PieceAliasRepository;
import uk.co.bbr.services.pieces.repo.PieceRepository;
import uk.co.bbr.services.pieces.sql.PieceSql;
import uk.co.bbr.services.pieces.sql.dto.BestPieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.OwnChoiceUsagePieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.PieceSqlDto;
import uk.co.bbr.services.pieces.sql.dto.PieceUsageCountSqlDto;
import uk.co.bbr.services.pieces.sql.dto.PiecesPerSectionSqlDto;
import uk.co.bbr.services.pieces.sql.dto.SetTestUsagePieceSqlDto;
import uk.co.bbr.services.pieces.types.PieceCategory;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PieceServiceImpl implements PieceService, SlugTools {

    private final PieceRepository pieceRepository;
    private final PieceAliasRepository pieceAliasRepository;
    private final SecurityService securityService;
    private final EntityManager entityManager;

    @Override
    @IsBbrMember
    public PieceDao create(PieceDao piece) {
        return this.create(piece, false);
    }

    private void validateMandatory(PieceDao piece) {
        // validation
        if (StringUtils.isBlank(piece.getName())) {
            throw new ValidationException("Piece name must be specified");
        }
    }

    private PieceDao create(PieceDao piece, boolean migrating) {
       this.validateMandatory(piece);

        // defaults
        if (StringUtils.isBlank(piece.getSlug())) {
            piece.setSlug(slugify(piece.getName()));
        }

        if (piece.getCategory() == null) {
            piece.setCategory(PieceCategory.TEST_PIECE);
        }

        if (piece.getId() != null) {
            throw new ValidationException("Can't create with specific id");
        }

        // does the slug already exist?
        Optional<PieceDao> slugMatches = this.pieceRepository.fetchBySlug(piece.getSlug());
        if (slugMatches.isPresent()) {
            throw new ValidationException("Piece with slug " + piece.getSlug() + " already exists.");
        }

        if (!migrating) {
            piece.setCreated(LocalDateTime.now());
            piece.setCreatedBy(this.securityService.getCurrentUsername());
            piece.setUpdated(LocalDateTime.now());
            piece.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        return this.pieceRepository.saveAndFlush(piece);
    }

    @Override
    @IsBbrMember
    public PieceDao create(String name, PieceCategory category, PersonDao composer) {
        PieceDao newPiece = new PieceDao();
        newPiece.setName(name);
        newPiece.setCategory(category);
        newPiece.setComposer(composer);
        return this.create(newPiece);
    }

    @Override
    @IsBbrMember
    public PieceDao create(String name) {
        PieceDao newPiece = new PieceDao();
        newPiece.setName(name);
        newPiece.setCategory(PieceCategory.TEST_PIECE);
        return this.create(newPiece);
    }

    @Override
    @IsBbrMember
    public PieceDao update(PieceDao piece) {
        this.validateMandatory(piece);

        piece.setUpdated(LocalDateTime.now());
        piece.setUpdatedBy(this.securityService.getCurrentUsername());
        return this.pieceRepository.saveAndFlush(piece);
    }

    @Override
    @IsBbrMember
    public void createAlternativeName(PieceDao piece, PieceAliasDao alternativeName) {
        this.createAlternativeName(piece, alternativeName, false);
    }

    private void createAlternativeName(PieceDao piece, PieceAliasDao alternativeName, boolean migrating) {
        alternativeName.setPiece(piece);
        if (!migrating) {
            alternativeName.setCreated(LocalDateTime.now());
            alternativeName.setCreatedBy(this.securityService.getCurrentUsername());
            alternativeName.setUpdated(LocalDateTime.now());
            alternativeName.setUpdatedBy(this.securityService.getCurrentUsername());
        }
        this.pieceAliasRepository.saveAndFlush(alternativeName);
    }

    @Override
    public Optional<PieceDao> fetchBySlug(String pieceSlug) {
        return this.pieceRepository.fetchBySlug(pieceSlug);
    }

    @Override
    public Optional<PieceDao> fetchById(Long pieceId) {
        return this.pieceRepository.fetchById(pieceId);
    }

    @Override
    public List<PieceAliasDao> fetchAlternateNames(PieceDao piece) {
        return this.pieceAliasRepository.findForPieceId(piece.getId());
    }

    @Override
    public PieceListDto listPiecesStartingWith(String prefix) {
        List<PieceDao> piecesToReturn;
        List<PieceUsageCountSqlDto> pieceUsageCounts;

        switch (prefix.toUpperCase()) {
            case "ALL" -> {
                piecesToReturn = this.pieceRepository.findAllOrderByName();
                pieceUsageCounts = PieceSql.selectAllPieceUsageCounts(this.entityManager);
            }
            case "0" -> {
                piecesToReturn = this.pieceRepository.findWithNumberPrefixOrderByName();
                pieceUsageCounts = PieceSql.selectNumbersPieceUsageCounts(this.entityManager);
            }
            default -> {
                if (prefix.strip().length() != 1) {
                    throw new UnsupportedOperationException("Prefix must be a single character");
                }
                piecesToReturn = this.pieceRepository.findByPrefixOrderByName(prefix.strip().toUpperCase());
                pieceUsageCounts = PieceSql.selectPieceUsageCounts(this.entityManager, prefix.toUpperCase());
            }
        }

        for (PieceDao eachPiece : piecesToReturn) {
            for (PieceUsageCountSqlDto eachCount : pieceUsageCounts) {
                if (eachPiece.getId().intValue() == eachCount.getPieceId().intValue()){
                    eachPiece.setOwnChoiceCount(eachCount.getOwnChoiceCount());
                    eachPiece.setSetTestCount(eachCount.getSetTestCount());
                }
            }
        }

        long allPiecesCount = this.pieceRepository.count();

        return new PieceListDto(piecesToReturn.size(), allPiecesCount, prefix, piecesToReturn);
    }

    @Override
    public PieceListDto listUnusedPieces() {
        List<PieceSqlDto> pieces = PieceSql.selectUnusedPieces(this.entityManager);

        List<PieceDao> piecesToReturn = new ArrayList<>();
        for (PieceSqlDto eachPiece : pieces) {
            piecesToReturn.add(eachPiece.asPiece());
        }

        long allPiecesCount = this.pieceRepository.count();

        return new PieceListDto(piecesToReturn.size(), allPiecesCount, "UNUSED", piecesToReturn);
    }

    @Override
    public List<PieceDao> findPiecesForPerson(PersonDao person) {
        return this.pieceRepository.findForPersonOrderByName(person.getId());
    }

    @Override
    public List<ContestResultPieceDao> fetchOwnChoicePieceUsage(PieceDao piece) {
        List<ContestResultPieceDao> resultPieces = new ArrayList<>();

        List<OwnChoiceUsagePieceSqlDto>  ownChoicePieces = PieceSql.selectOwnChoicePieceUsage(this.entityManager, piece.getId());
        for (OwnChoiceUsagePieceSqlDto eachSqlRow : ownChoicePieces) {
            ContestResultPieceDao contestResultPiece = new ContestResultPieceDao();
            contestResultPiece.setPiece(new PieceDao());
            contestResultPiece.setContestResult(new ContestResultDao());
            contestResultPiece.getContestResult().setBand(new BandDao());
            contestResultPiece.getContestResult().setContestEvent(new ContestEventDao());
            contestResultPiece.getContestResult().getContestEvent().setContest(new ContestDao());

            contestResultPiece.getContestResult().getContestEvent().setEventDate(eachSqlRow.getEventDate());
            contestResultPiece.getContestResult().getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(eachSqlRow.getEventDateResolution()));

            contestResultPiece.getContestResult().getContestEvent().getContest().setSlug(eachSqlRow.getContestSlug());
            contestResultPiece.getContestResult().getContestEvent().getContest().setName(eachSqlRow.getContestName());

            contestResultPiece.getContestResult().setBandName(eachSqlRow.getBandCompetedAs());
            contestResultPiece.getContestResult().getBand().setName(eachSqlRow.getBandName());
            contestResultPiece.getContestResult().getBand().setSlug(eachSqlRow.getBandSlug());
            contestResultPiece.getContestResult().getBand().setRegion(new RegionDao());
            contestResultPiece.getContestResult().getBand().getRegion().setCountryCode(eachSqlRow.getBandCountryCode());
            contestResultPiece.getContestResult().getBand().getRegion().setName(eachSqlRow.getBandRegionName());
            contestResultPiece.getContestResult().setPosition(String.valueOf(eachSqlRow.getResultPosition()));
            contestResultPiece.getContestResult().setResultPositionType(ResultPositionType.fromCode(eachSqlRow.getResultPositionType()));
            contestResultPiece.getContestResult().setResultAward(ResultAwardType.fromCode(eachSqlRow.getResultAward()));
            contestResultPiece.getContestResult().setId(eachSqlRow.getContestResultId().longValue());
            contestResultPiece.getContestResult().getContestEvent().setId(eachSqlRow.getContestEventId().longValue());

            resultPieces.add(contestResultPiece);
        }
        return resultPieces;
    }

    @Override
    public List<ContestEventTestPieceDao> fetchSetTestPieceUsage(PieceDao piece) {
        List<ContestEventTestPieceDao> resultPieces = new ArrayList<>();

        List<SetTestUsagePieceSqlDto> setTestPieces = PieceSql.selectSetTestPieceUsage(this.entityManager, piece.getId());
        for (SetTestUsagePieceSqlDto eachSqlRow : setTestPieces) {
            ContestEventTestPieceDao setTestPiece = new ContestEventTestPieceDao();
            setTestPiece.setPiece(new PieceDao());
            setTestPiece.setContestEvent(new ContestEventDao());
            setTestPiece.getContestEvent().setContest(new ContestDao());

            setTestPiece.getContestEvent().setEventDate(eachSqlRow.getEventDate());
            setTestPiece.getContestEvent().setEventDateResolution(ContestEventDateResolution.fromCode(eachSqlRow.getEventDateResolution()));

            setTestPiece.getContestEvent().getContest().setSlug(eachSqlRow.getContestSlug());
            setTestPiece.getContestEvent().getContest().setName(eachSqlRow.getContestName());
            setTestPiece.getContestEvent().setId(eachSqlRow.getContestEventId().longValue());

            BandDao winningBand = new BandDao();
            winningBand.setName(eachSqlRow.getBandCompetedAs());
            winningBand.setSlug(eachSqlRow.getBandSlug());
            winningBand.setRegion(new RegionDao());
            winningBand.getRegion().setCountryCode(eachSqlRow.getBandCountryCode());
            winningBand.getRegion().setName(eachSqlRow.getBandRegionName());

            setTestPiece.getWinners().add(winningBand);

            resultPieces.add(setTestPiece);
        }

        return resultPieces;
    }

    @Override
    public List<BestOwnChoiceDto> fetchMostSuccessfulOwnChoice() {
        List<BestPieceSqlDto> bestPieceRawData = PieceSql.mostSuccessfulOwnChoice(this.entityManager);

        List<BestOwnChoiceDto> returnData = new ArrayList<>();
        for (BestPieceSqlDto eachSqlResult : bestPieceRawData) {
            BestOwnChoiceDto foundPiece = null;
            for (BestOwnChoiceDto eachPiece : returnData) {
                if (eachPiece.getPiece().getSlug().equals(eachSqlResult.getPieceSlug())) {
                    foundPiece = eachPiece;
                    break;
                }
            }

            if (foundPiece == null) {
                int topThree = 1;
                int points = this.pointsFromPosition(eachSqlResult.getResultPosition());
                BestOwnChoiceDto newResult = new BestOwnChoiceDto(eachSqlResult.getPiece(), topThree, points);
                returnData.add(newResult);
            } else {
                int newPoints = foundPiece.getPoints() + this.pointsFromPosition(eachSqlResult.getResultPosition());
                int newTopThree = foundPiece.getTopThree() + 1;

                foundPiece.setPoints(newPoints);
                foundPiece.setTopThree(newTopThree);
            }
        }

        return returnData.stream().sorted(Comparator.comparing(BestOwnChoiceDto::getPoints).reversed()).collect(Collectors.toList());
    }

    private int pointsFromPosition(Integer resultPosition) {
        return switch (resultPosition) {
            case 1 -> 10;
            case 2 -> 5;
            case 3 -> 3;
            default -> 0;
        };
    }

    @Override
    public List<PiecesPerSectionSqlDto> fetchPiecesForSection(SectionDao section) {
        return PieceSql.piecesForSection(this.entityManager, section.getSlug());
    }

    @Override
    public void delete(PieceDao piece) {
        List<PieceAliasDao> pieceAliases = this.pieceAliasRepository.findForPieceId(piece.getId());
        this.pieceAliasRepository.deleteAll(pieceAliases);

        this.pieceRepository.delete(piece);
    }
}
