package uk.co.bbr.web.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:events-edit-result-anon-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EditResultAnonymousWebTests implements LoginMixin {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private BandService bandService;
    @Autowired
    private PersonService personService;
    @Autowired
    private ContestEventService contestEventService;
    @Autowired
    private ResultService contestResultService;
    @Autowired
    private RestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @Test
    void testEditResultFailsIfNotLoggedIn() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        ContestDao northWestArea = this.contestService.create("North West Area 3");
        ContestEventDao northWestArea2010 = this.contestEventService.create(northWestArea, LocalDate.of(2010, 3, 3));
        BandDao grimethorpe = this.bandService.create("Grimethorpe 3");
        PersonDao duncanBeckley = this.personService.create("Beckley 3", "Duncan");
        ContestResultDao result = this.contestResultService.addResult(northWestArea2010, "4", grimethorpe, duncanBeckley);

        logoutTestUser();
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/north-west-area-3/2010-03-03/result/" + result.getId() + "/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("To access this page you need to be logged into an account on the site."));
    }
}
