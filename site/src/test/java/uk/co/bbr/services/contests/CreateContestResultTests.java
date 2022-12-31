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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=create-result-tests-h2", "spring.datasource.url=jdbc:h2:mem:create-result-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
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

        ContestDao contest = this.contestService.create("Yorkshire Area");
        ContestEventDao event = this.contestEventService.create(contest, LocalDate.of(2020, 03, 03));
        BandDao band = this.bandService.create("Rothwell Temperance");
        PersonDao conductor = this.personService.create("Roberts", "David");

        ContestResultDao newResult = new ContestResultDao();
        newResult.setBand(band);
        newResult.setPosition(1);
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
        assertEquals("David Roberts", result.getConductorName());
        assertEquals(1, result.getPosition());
        assertEquals(ResultPositionType.RESULT, result.getResultPositionType());
        assertEquals(2, result.getDraw());

        logoutTestUser();
    }
}


