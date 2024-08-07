package uk.co.bbr.services.contests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.events.ContestEventService;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contests-create-result-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class CreateContestResultTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService resultService;
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
        ContestResultDao result = this.resultService.addResult(event, newResult);

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

        ContestDao contest = this.contestService.create("  Welsh   Area      ");
        ContestEventDao event = this.contestEventService.create(contest, LocalDate.of(2020, 3, 3));
        BandDao band = this.bandService.create("  Black   Dyke   Band");
        PersonDao conductor1 = this.personService.create("Roberts", "John");
        PersonDao conductor2 = this.personService.create("Childs", "Bob");
        PersonDao conductor3 = this.personService.create("Childs", "Nick");

        ContestResultDao existingResult = new ContestResultDao();
        existingResult.setBand(band);
        existingResult.setBandName(band.getName());
        this.resultService.addResult(event, existingResult);

        // act
        ContestResultDao replaceResult = new ContestResultDao();
        replaceResult.setBand(band);
        replaceResult.setBandName(band.getName());
        replaceResult.setDraw(2);
        replaceResult.setDrawSecond(3);
        replaceResult.setDrawThird(4);
        replaceResult.setNotes(" New Note ");
        replaceResult.setPointsTotal(" 234 ");
        replaceResult.setPointsFirst(" 1st Points ");
        replaceResult.setPointsSecond(" 2nd Points ");
        replaceResult.setPointsThird(" 3rd Points ");
        replaceResult.setPointsFourth(" 4th Points ");
        replaceResult.setPointsFifth(" 5th Points ");
        replaceResult.setPointsPenalty(" Penalty  ");
        replaceResult.setPosition("1");
        replaceResult.setConductor(conductor1);
        replaceResult.setConductorSecond(conductor2);
        replaceResult.setConductorThird(conductor3);
        ContestResultDao result = this.resultService.addResult(event, replaceResult);

        // assert
        ContestEventDao fetchedEvent = this.contestEventService.fetchEvent(event.getContest().getSlug(), event.getEventDate()).get();
        List<ContestResultDao> results = this.resultService.fetchForEvent(fetchedEvent);
        assertEquals(1, results.size());

        assertEquals(existingResult.getId(), results.get(0).getId());
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
        assertEquals("5th Points", results.get(0).getPointsFifth());
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
        existingResult.setPointsFifth(" 5th Points ");
        existingResult.setPointsPenalty(" Penalty   ");
        existingResult.setPosition("1");
        existingResult.setConductor(conductor1);
        existingResult.setConductorSecond(conductor2);
        existingResult.setConductorThird(conductor3);
        this.resultService.addResult(event, existingResult);

        // act
        ContestResultDao replaceResult = new ContestResultDao();
        replaceResult.setBand(band);
        replaceResult.setBandName("Rothwell Temperance");
        replaceResult.setDraw(3);
        replaceResult.setDrawSecond(4);
        replaceResult.setDrawThird(5);
        replaceResult.setNotes("Altered Note");
        replaceResult.setPointsTotal("999");
        replaceResult.setPointsFirst("1st");
        replaceResult.setPointsSecond("2nd");
        replaceResult.setPointsThird("3rd");
        replaceResult.setPointsFourth("4th ");
        replaceResult.setPointsFifth("5th ");
        replaceResult.setPointsPenalty("Updated");
        replaceResult.setPosition("5");
        replaceResult.setConductor(conductor3);
        replaceResult.setConductorSecond(conductor1);
        replaceResult.setConductorThird(conductor2);
        ContestResultDao result = this.resultService.addResult(event, replaceResult);

        // assert
        ContestEventDao fetchedEvent = this.contestEventService.fetchEvent(event.getContest().getSlug(), event.getEventDate()).get();
        List<ContestResultDao> results = this.resultService.fetchForEvent(fetchedEvent);
        assertEquals(1, results.size());

        assertEquals(existingResult.getId(), results.get(0).getId());
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
        assertEquals("5th Points", results.get(0).getPointsFifth());
        assertEquals("Penalty", results.get(0).getPointsPenalty());
        assertEquals("New Note", results.get(0).getNotes());

        logoutTestUser();
    }

    @Test
    void testAddingSameBandContestResultWithDifferentBandNameSucceeds() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao contest = this.contestService.create("  London   Area      ");
        ContestEventDao event = this.contestEventService.create(contest, LocalDate.of(2020, 3, 3));
        BandDao band = this.bandService.create("  Black Dyke ");
        PersonDao conductor1 = this.personService.create("Simpson", "Peter");
        PersonDao conductor2 = this.personService.create("Poet", "David");
        PersonDao conductor3 = this.personService.create("Beckley", "Richard");

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
        existingResult.setPointsFifth(" 5th Points ");
        existingResult.setPointsPenalty(" Penalty   ");
        existingResult.setPosition("1");
        existingResult.setConductor(conductor1);
        existingResult.setConductorSecond(conductor2);
        existingResult.setConductorThird(conductor3);
        this.resultService.addResult(event, existingResult);

        // act
        ContestResultDao replaceResult = new ContestResultDao();
        replaceResult.setBand(band);
        replaceResult.setBandName("Wallace Arnold (Rothwell)");
        replaceResult.setDraw(13);
        replaceResult.setDrawSecond(14);
        replaceResult.setDrawThird(15);
        replaceResult.setNotes("Altered Note 2");
        replaceResult.setPointsTotal("9992");
        replaceResult.setPointsFirst("1st 2");
        replaceResult.setPointsSecond("2nd 2");
        replaceResult.setPointsThird("3rd 2");
        replaceResult.setPointsFourth("4th  2");
        replaceResult.setPointsFifth("5th  2");
        replaceResult.setPointsPenalty("Updated 2");
        replaceResult.setPosition("10");
        replaceResult.setConductor(conductor1);
        replaceResult.setConductorSecond(conductor2);
        replaceResult.setConductorThird(conductor3);
        ContestResultDao result = this.resultService.addResult(event, replaceResult);

        // assert
        ContestEventDao fetchedEvent = this.contestEventService.fetchEvent(event.getContest().getSlug(), event.getEventDate()).get();
        List<ContestResultDao> results = this.resultService.fetchForEvent(fetchedEvent);
        assertEquals(2, results.size());

        assertEquals(existingResult.getId(), results.get(0).getId());
        assertEquals("Black Dyke", results.get(0).getBand().getName());
        assertEquals("Rothwell Temperance", results.get(0).getBandName());
        assertEquals("Peter Simpson", results.get(0).getConductor().getName());
        assertEquals("Peter Simpson", results.get(0).getOriginalConductorName());
        assertEquals("David Poet", results.get(0).getConductorSecond().getName());
        assertEquals("Richard Beckley", results.get(0).getConductorThird().getName());
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
        assertEquals("5th Points", results.get(0).getPointsFifth());
        assertEquals("Penalty", results.get(0).getPointsPenalty());
        assertEquals("New Note", results.get(0).getNotes());

        assertNotEquals(existingResult.getId(), results.get(1).getId());
        assertEquals("Black Dyke", results.get(1).getBand().getName());
        assertEquals("Wallace Arnold (Rothwell)", results.get(1).getBandName());
        assertEquals("Peter Simpson", results.get(1).getConductor().getName());
        assertEquals("Peter Simpson", results.get(1).getOriginalConductorName());
        assertEquals("David Poet", results.get(1).getConductorSecond().getName());
        assertEquals("Richard Beckley", results.get(1).getConductorThird().getName());
        assertEquals(10, results.get(1).getPosition());
        assertEquals(ResultPositionType.RESULT, results.get(1).getResultPositionType());
        assertEquals(13, results.get(1).getDraw());
        assertEquals(14, results.get(1).getDrawSecond());
        assertEquals(15, results.get(1).getDrawThird());
        assertEquals("9992", results.get(1).getPointsTotal());
        assertEquals("1st 2", results.get(1).getPointsFirst());
        assertEquals("2nd 2", results.get(1).getPointsSecond());
        assertEquals("3rd 2", results.get(1).getPointsThird());
        assertEquals("4th  2", results.get(1).getPointsFourth());
        assertEquals("5th  2", results.get(1).getPointsFifth());
        assertEquals("Updated 2", results.get(1).getPointsPenalty());
        assertEquals("Altered Note 2", results.get(1).getNotes());

        logoutTestUser();
    }
}





