package uk.co.bbr.pages.contests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestEventService;
import uk.co.bbr.services.contests.ContestGroupService;
import uk.co.bbr.services.contests.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestGroupDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.dto.ContestGroupYearDto;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.services.contests.types.TestPieceAndOr;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=contest-group-year-page-service-tests-h2", "spring.datasource.url=jdbc:h2:mem:contest-group-year-page-service-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestGroupYearPageTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestResultService contestResultService;
    @Autowired private PieceService pieceService;
    @Autowired private BandService bandService;
    @Autowired private PersonService personService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band1 = this.bandService.create("Band 1");
        BandDao band2 = this.bandService.create("Band 2");
        BandDao band3 = this.bandService.create("Band 3");
        BandDao band4 = this.bandService.create("Band 4");
        BandDao band5 = this.bandService.create("Band 5");
        BandDao band6 = this.bandService.create("Band 6");

        PersonDao conductorA = this.personService.create("Conductor", "A");
        PersonDao conductorB = this.personService.create("Conductor", "B");
        PersonDao conductorC = this.personService.create("Conductor", "C");
        PersonDao conductorD = this.personService.create("Conductor", "D");

        PieceDao piece1 = this.pieceService.create("Piece 1");
        PieceDao piece2 = this.pieceService.create("Piece 2");
        PieceDao piece3 = this.pieceService.create("Piece 3");
        PieceDao piece4 = this.pieceService.create("Piece 4");
        PieceDao piece5 = this.pieceService.create("Piece 5");
        PieceDao piece6 = this.pieceService.create("Piece 6");
        PieceDao piece7 = this.pieceService.create("Piece 7");

        ContestGroupDao group = this.contestGroupService.create("Test Group");

        ContestDao contestA = this.contestService.create("Test Contest Section A", group, 10);
        ContestDao contestD = this.contestService.create("Test Contest Section D", group, 40);
        ContestDao contestC = this.contestService.create("Test Contest Section C", group, 30);
        ContestDao contestB = this.contestService.create("Test Contest Section B", group, 20);

        ContestEventDao contestA1 = this.contestEventService.create(contestA, LocalDate.of(2000, 3, 31));
        this.contestEventService.addTestPieceToContest(contestA1, piece1);
        this.contestResultService.addResult(contestA1, "1", band1, conductorD);
        this.contestResultService.addResult(contestA1, "2", band2, conductorC);
        ContestEventDao contestB1 = this.contestEventService.create(contestB, LocalDate.of(2000, 3, 31));
        this.contestEventService.addTestPieceToContest(contestB1, piece2);
        this.contestResultService.addResult(contestB1, "1", band2, conductorC);
        this.contestResultService.addResult(contestB1, "W", band3, conductorA);
        ContestEventDao contestC1 = this.contestEventService.create(contestC, LocalDate.of(2000, 3, 31));
        this.contestEventService.addTestPieceToContest(contestC1, piece3);
        this.contestResultService.addResult(contestC1, "1", band3, conductorB);
        this.contestResultService.addResult(contestC1, "0", band4, conductorA);
        ContestEventDao contestD1 = this.contestEventService.create(contestD, LocalDate.of(2000, 3, 31));
        this.contestEventService.addTestPieceToContest(contestD1, piece4);
        this.contestResultService.addResult(contestD1, "1", band4, conductorA);
        this.contestResultService.addResult(contestD1, "1", band5, conductorD);
        this.contestResultService.addResult(contestD1, "5", band3, conductorB);

        ContestEventDao contestA2 = this.contestEventService.create(contestA, LocalDate.of(1999, 4, 30));
        this.contestEventService.addTestPieceToContest(contestA2, piece5);
        this.contestResultService.addResult(contestA2, "1", band6, conductorD);
        ContestEventDao contestB2 = this.contestEventService.create(contestB, LocalDate.of(1999, 4, 30));
        this.contestEventService.addTestPieceToContest(contestB2, piece6);
        this.contestEventService.addTestPieceToContest(contestB2, piece7, TestPieceAndOr.OR);
        this.contestResultService.addResult(contestB2, "1", band5, conductorB);
        ContestEventDao contestC2 = this.contestEventService.create(contestC, LocalDate.of(1999, 4, 27));
        ContestResultDao result = this.contestResultService.addResult(contestC2, "1", band4, null);
        this.contestResultService.addPieceToResult(result, piece1);

        ContestEventDao contestA3 = this.contestEventService.create(contestA, LocalDate.of(1995, 3, 26));
        ContestEventDao contestC3 = this.contestEventService.create(contestC, LocalDate.of(1995, 3, 27));

        ContestEventDao contestD4 = this.contestEventService.create(contestD, LocalDate.of(1990, 3, 28));

        logoutTestUser();
    }

    @Test
    void testFetchEarliestYearReturnsSuccessfully() {
        // act
        ContestGroupYearDto eventsForGroupAndYear = this.contestGroupService.fetchEventsByGroupSlugAndYear("test-group", 1990);

        // assert
        assertEquals("Test Group", eventsForGroupAndYear.getContestGroup().getName());
        assertEquals(1990, eventsForGroupAndYear.getYear());
        assertEquals(1995, eventsForGroupAndYear.getNextYear());
        assertNull(eventsForGroupAndYear.getPreviousYear());

        assertEquals(1, eventsForGroupAndYear.getContestEvents().size());

        assertEquals("Test Contest Section D", eventsForGroupAndYear.getContestEvents().get(0).getContestEvent().getContest().getName());
        assertEquals(0, eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().size());
        assertEquals(0, eventsForGroupAndYear.getContestEvents().get(0).getTestPieces().size());
    }

    @Test
    void testFetchLastButOneYearReturnsSuccessfully() {
        // act
        ContestGroupYearDto eventsForGroupAndYear = this.contestGroupService.fetchEventsByGroupSlugAndYear("test-group", 1999);

        // assert
        assertEquals("Test Group", eventsForGroupAndYear.getContestGroup().getName());
        assertEquals(1999, eventsForGroupAndYear.getYear());
        assertEquals(2000, eventsForGroupAndYear.getNextYear());
        assertEquals(1995, eventsForGroupAndYear.getPreviousYear());

        assertEquals(3, eventsForGroupAndYear.getContestEvents().size());

        assertEquals("Test Contest Section A", eventsForGroupAndYear.getContestEvents().get(0).getContestEvent().getContest().getName());
        assertEquals(1, eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().size());
        assertEquals("Band 6", eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().get(0).getBandName());
        assertEquals("Band 6", eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().get(0).getBand().getName());
        assertEquals("D Conductor", eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().get(0).getOriginalConductorName());
        assertEquals("D", eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().get(0).getConductor().getFirstNames());
        assertEquals("Piece 5", eventsForGroupAndYear.getContestEvents().get(0).getTestPieces().get(0).getName());

        assertEquals("Test Contest Section B", eventsForGroupAndYear.getContestEvents().get(1).getContestEvent().getContest().getName());
        assertEquals(1, eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().size());
        assertEquals("Band 5", eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().get(0).getBandName());
        assertEquals("Band 5", eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().get(0).getBand().getName());
        assertEquals("B Conductor", eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().get(0).getOriginalConductorName());
        assertEquals("B", eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().get(0).getConductor().getFirstNames());
        assertEquals("Piece 6", eventsForGroupAndYear.getContestEvents().get(1).getTestPieces().get(0).getName());
        assertEquals("Piece 7", eventsForGroupAndYear.getContestEvents().get(1).getTestPieces().get(1).getName());

        assertEquals("Test Contest Section C", eventsForGroupAndYear.getContestEvents().get(2).getContestEvent().getContest().getName());
        assertEquals(1, eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().size());
        assertEquals("Band 4", eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().get(0).getBandName());
        assertEquals("Band 4", eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().get(0).getBand().getName());
        assertNull(eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().get(0).getConductor());
        assertEquals(1, eventsForGroupAndYear.getContestEvents().get(2).getTestPieces().size());
        assertEquals("Piece 1", eventsForGroupAndYear.getContestEvents().get(2).getTestPieces().get(0).getName());
    }


    @Test
    void testFetchLatestYearReturnsSuccessfully() {
        // act
        ContestGroupYearDto eventsForGroupAndYear = this.contestGroupService.fetchEventsByGroupSlugAndYear("test-group", 2000);

        // assert
        assertEquals("Test Group", eventsForGroupAndYear.getContestGroup().getName());
        assertEquals(2000, eventsForGroupAndYear.getYear());
        assertNull(eventsForGroupAndYear.getNextYear());
        assertEquals(1999, eventsForGroupAndYear.getPreviousYear());

        assertEquals(4, eventsForGroupAndYear.getContestEvents().size());

        assertEquals("Test Contest Section A", eventsForGroupAndYear.getContestEvents().get(0).getContestEvent().getContest().getName());
        assertEquals(1, eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().size());
        assertEquals("Band 1", eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().get(0).getBandName());
        assertEquals("Band 1", eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().get(0).getBand().getName());
        assertEquals("D Conductor", eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().get(0).getOriginalConductorName());
        assertEquals("D", eventsForGroupAndYear.getContestEvents().get(0).getWinningBands().get(0).getConductor().getFirstNames());
        assertEquals("Piece 1", eventsForGroupAndYear.getContestEvents().get(0).getTestPieces().get(0).getName());

        assertEquals("Test Contest Section B", eventsForGroupAndYear.getContestEvents().get(1).getContestEvent().getContest().getName());
        assertEquals(1, eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().size());
        assertEquals("Band 2", eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().get(0).getBandName());
        assertEquals("Band 2", eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().get(0).getBand().getName());
        assertEquals("C Conductor", eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().get(0).getOriginalConductorName());
        assertEquals("C", eventsForGroupAndYear.getContestEvents().get(1).getWinningBands().get(0).getConductor().getFirstNames());
        assertEquals("Piece 2", eventsForGroupAndYear.getContestEvents().get(1).getTestPieces().get(0).getName());

        assertEquals("Test Contest Section C", eventsForGroupAndYear.getContestEvents().get(2).getContestEvent().getContest().getName());
        assertEquals(1, eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().size());
        assertEquals("Band 3", eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().get(0).getBandName());
        assertEquals("Band 3", eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().get(0).getBand().getName());
        assertEquals("B Conductor", eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().get(0).getOriginalConductorName());
        assertEquals("B", eventsForGroupAndYear.getContestEvents().get(2).getWinningBands().get(0).getConductor().getFirstNames());
        assertEquals("Piece 3", eventsForGroupAndYear.getContestEvents().get(2).getTestPieces().get(0).getName());

        assertEquals("Test Contest Section D", eventsForGroupAndYear.getContestEvents().get(3).getContestEvent().getContest().getName());
        assertEquals(2, eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().size());
        assertEquals("Band 4", eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().get(0).getBandName());
        assertEquals("Band 4", eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().get(0).getBand().getName());
        assertEquals("A Conductor", eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().get(0).getOriginalConductorName());
        assertEquals("A", eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().get(0).getConductor().getFirstNames());

        assertEquals("Band 5", eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().get(1).getBandName());
        assertEquals("Band 5", eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().get(1).getBand().getName());
        assertEquals("D Conductor", eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().get(1).getOriginalConductorName());
        assertEquals("D", eventsForGroupAndYear.getContestEvents().get(3).getWinningBands().get(1).getConductor().getFirstNames());

        assertEquals("Piece 4", eventsForGroupAndYear.getContestEvents().get(3).getTestPieces().get(0).getName());
    }



}



