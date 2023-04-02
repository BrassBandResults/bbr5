package uk.co.bbr.services.contests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestEventDao;
import uk.co.bbr.services.contests.dao.ContestResultDao;
import uk.co.bbr.services.contests.types.ResultPositionType;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=create-result-tests-h2", "spring.datasource.url=jdbc:h2:mem:create-result-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
class CreateContestResultTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestResultService contestResultService;
    @Autowired private BandService bandService;
    @Autowired private PersonService personService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreatingSingleContestResultWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao contest = this.contestService.create("  Yorkshire   Area      ");
        ContestEventDao event = this.contestEventService.create(contest, LocalDate.of(2020, 03, 03));
        BandDao band = this.bandService.create("Rothwell Temperance");
        PersonDao conductor = this.personService.create("Roberts", "David");

        ContestResultDao newResult = new ContestResultDao();
        newResult.setBand(band);
        newResult.setBandName("  Rothwell  Temperance  ");
        newResult.setPosition("1");
        newResult.setConductor(conductor);
        newResult.setDraw(2);

        // act
        ContestResultDao result = this.contestResultService.addResult(event, newResult);

        // assert
        assertEquals("Yorkshire Area", result.getContestEvent().getContest().getName());
        assertEquals(LocalDate.of(2020, 3, 3), result.getContestEvent().getEventDate());
        assertEquals("Rothwell Temperance", result.getBand().getName());
        assertEquals("Rothwell Temperance", result.getBandName());
        assertEquals("David Roberts", result.getConductor().getName());
        assertEquals("David Roberts", result.getOriginalConductorName());
        assertEquals(1, result.getPosition());
        assertEquals(ResultPositionType.RESULT, result.getResultPositionType());
        assertEquals(2, result.getDraw());

        logoutTestUser();
    }

    @Test
    void testAddingSameBandContestResultMergesResultRows() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao contest = this.contestService.create("  London   Area      ");
        ContestEventDao event = this.contestEventService.create(contest, LocalDate.of(2020, 3, 3));
        BandDao band = this.bandService.create("  Black   Dyke   Band");
        PersonDao conductor1 = this.personService.create("Roberts", "John");
        PersonDao conductor2 = this.personService.create("Childs", "Bob");
        PersonDao conductor3 = this.personService.create("Childs", "Nick");

        ContestResultDao existingResult = new ContestResultDao();
        existingResult.setBand(band);
        existingResult.setBandName(band.getName());
        this.contestResultService.addResult(event, existingResult);

        // act
        ContestResultDao replaceResult = new ContestResultDao();
        replaceResult.setBand(band);

        replaceResult.setDraw(2);
        replaceResult.setDrawSecond(3);
        replaceResult.setDrawThird(4);
        replaceResult.setNotes(" New Note ");
        replaceResult.setPointsTotal(" 234 ");
        replaceResult.setPointsFirst(" 1st Points ");
        replaceResult.setPointsSecond(" 2nd Points ");
        replaceResult.setPointsThird(" 3rd Points ");
        replaceResult.setPointsFourth(" 4th Points ");
        replaceResult.setPointsPenalty(" Penalty  ");
        replaceResult.setPosition("1");
        replaceResult.setConductor(conductor1);
        replaceResult.setConductorSecond(conductor2);
        replaceResult.setConductorThird(conductor3);
        ContestResultDao result = this.contestResultService.addResult(event, replaceResult);

        // assert
        ContestEventDao fetchedEvent = this.contestEventService.fetchEvent(event.getContest().getSlug(), event.getEventDate()).get();
        List<ContestResultDao> results = this.contestResultService.fetchForEvent(fetchedEvent);
        assertEquals(1, results.size());

        assertEquals(existingResult.getId(), results.get(0).getId());
        assertEquals("London Area", results.get(0).getContestEvent().getContest().getName());
        assertEquals(LocalDate.of(2020, 3, 3), results.get(0).getContestEvent().getEventDate());
        assertEquals("Black Dyke Band", results.get(0).getBand().getName());
        assertEquals("Black Dyke Band", results.get(0).getBandName());
        assertEquals("John Roberts", results.get(0).getConductor().getName());
        assertEquals("John Roberts", results.get(0).getOriginalConductorName());
        assertEquals("Bob Childs", results.get(0).getConductorSecond().getName());
        assertEquals("Nick Childs", results.get(0).getConductorThird().getName());
        assertEquals(1, results.get(0).getPosition());
        assertEquals(ResultPositionType.RESULT, results.get(0).getResultPositionType());
        assertEquals(2, results.get(0).getDraw());
        assertEquals(3, results.get(0).getDrawSecond());
        assertEquals(4, results.get(0).getDrawThird());
        assertEquals("234", results.get(0).getPointsTotal());
        assertEquals("1st Points", results.get(0).getPointsFirst());
        assertEquals("2nd Points", results.get(0).getPointsSecond());
        assertEquals("3rd Points", results.get(0).getPointsThird());
        assertEquals("4th Points", results.get(0).getPointsFourth());
        assertEquals("Penalty", results.get(0).getPointsPenalty());
        assertEquals("New Note", results.get(0).getNotes());

        logoutTestUser();
    }

    @Test
    void testAddingSameBandContestResultDoesntOverwriteExistingValues() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao contest = this.contestService.create("  North West   Area      ");
        ContestEventDao event = this.contestEventService.create(contest, LocalDate.of(2020, 3, 3));
        BandDao band = this.bandService.create("  Grimethorpe ");
        PersonDao conductor1 = this.personService.create("Roberts", "Peter");
        PersonDao conductor2 = this.personService.create("Childs", "David");
        PersonDao conductor3 = this.personService.create("Childs", "Richard");

        ContestResultDao existingResult = new ContestResultDao();
        existingResult.setBand(band);
        existingResult.setBandName("  Rothwell   Temperance  ");
        existingResult.setDraw(2);
        existingResult.setDrawSecond(3);
        existingResult.setDrawThird(4);
        existingResult.setNotes(" New Note ");
        existingResult.setPointsTotal(" 234 ");
        existingResult.setPointsFirst(" 1st Points ");
        existingResult.setPointsSecond(" 2nd Points ");
        existingResult.setPointsThird(" 3rd Points ");
        existingResult.setPointsFourth(" 4th Points ");
        existingResult.setPointsPenalty(" Penalty   ");
        existingResult.setPosition("1");
        existingResult.setConductor(conductor1);
        existingResult.setConductorSecond(conductor2);
        existingResult.setConductorThird(conductor3);
        this.contestResultService.addResult(event, existingResult);

        // act
        ContestResultDao replaceResult = new ContestResultDao();
        replaceResult.setBand(band);
        replaceResult.setDraw(3);
        replaceResult.setDrawSecond(4);
        replaceResult.setDrawThird(5);
        replaceResult.setNotes("Altered Note");
        replaceResult.setPointsTotal("999");
        replaceResult.setPointsFirst("1st Updated");
        replaceResult.setPointsSecond("2nd Updated");
        replaceResult.setPointsThird("3rd Updated");
        replaceResult.setPointsFourth("4th  Updated");
        replaceResult.setPointsPenalty("Updated");
        replaceResult.setPosition("5");
        replaceResult.setConductor(conductor3);
        replaceResult.setConductorSecond(conductor1);
        replaceResult.setConductorThird(conductor2);
        ContestResultDao result = this.contestResultService.addResult(event, replaceResult);

        // assert
        ContestEventDao fetchedEvent = this.contestEventService.fetchEvent(event.getContest().getSlug(), event.getEventDate()).get();
        List<ContestResultDao> results = this.contestResultService.fetchForEvent(fetchedEvent);
        assertEquals(1, results.size());

        assertEquals(existingResult.getId(), results.get(0).getId());
        assertEquals("North West Area", results.get(0).getContestEvent().getContest().getName());
        assertEquals(LocalDate.of(2020, 3, 3), results.get(0).getContestEvent().getEventDate());
        assertEquals("Grimethorpe", results.get(0).getBand().getName());
        assertEquals("Rothwell Temperance", results.get(0).getBandName());
        assertEquals("Peter Roberts", results.get(0).getConductor().getName());
        assertEquals("Peter Roberts", results.get(0).getOriginalConductorName());
        assertEquals("David Childs", results.get(0).getConductorSecond().getName());
        assertEquals("Richard Childs", results.get(0).getConductorThird().getName());
        assertEquals(1, results.get(0).getPosition());
        assertEquals(ResultPositionType.RESULT, results.get(0).getResultPositionType());
        assertEquals(2, results.get(0).getDraw());
        assertEquals(3, results.get(0).getDrawSecond());
        assertEquals(4, results.get(0).getDrawThird());
        assertEquals("234", results.get(0).getPointsTotal());
        assertEquals("1st Points", results.get(0).getPointsFirst());
        assertEquals("2nd Points", results.get(0).getPointsSecond());
        assertEquals("3rd Points", results.get(0).getPointsThird());
        assertEquals("4th Points", results.get(0).getPointsFourth());
        assertEquals("Penalty", results.get(0).getPointsPenalty());
        assertEquals("New Note", results.get(0).getNotes());

        logoutTestUser();
    }
}




