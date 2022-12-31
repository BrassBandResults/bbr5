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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=create-event-tests-h2", "spring.datasource.url=jdbc:h2:mem:create-event-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class CreateContestEventTests implements LoginMixin {

    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreatingSingleContestEventWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao contest = this.contestService.create("Yorkshire Area");

        // act
        ContestEventDao event = this.contestEventService.create(contest, LocalDate.of(2021, 4, 4));

        // assert
        assertNotNull(event.getContest());
        assertEquals("Yorkshire Area", event.getContest().getName());
        assertNotNull(event.getContest().getId());
        assertEquals(contest.getId(), event.getContest().getId());
        assertEquals(contest.getSlug(), event.getContest().getSlug());
        assertEquals(LocalDate.of(2021, 4, 4), event.getEventDate());
        assertNotNull(event.getContestType());
        assertEquals(contest.getDefaultContestType(), event.getContestType());

        logoutTestUser();
    }
}


