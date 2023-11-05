package uk.co.bbr.web.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
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
        "spring.datasource.url=jdbc:h2:mem:events-edit-result-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EditResultWebTests implements LoginMixin {

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
    private CsrfTokenRepository csrfTokenRepository;
    @Autowired
    private RestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testEditResultGetWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        ContestDao northWestArea = this.contestService.create("North West Area 1");
        ContestEventDao northWestArea2010 = this.contestEventService.create(northWestArea, LocalDate.of(2010, 3, 1));
        BandDao grimethorpe = this.bandService.create("Grimethorpe 1");
        PersonDao duncanBeckley = this.personService.create("Beckley 1", "Duncan");
        ContestResultDao result = this.contestResultService.addResult(northWestArea2010, "4", grimethorpe, duncanBeckley);

        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/north-west-area-1/2010-03-01/result/" + result.getId() + "/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Result"));
        assertTrue(response.contains("<form action=\"/contests/north-west-area-1/2010-03-01/result/" + result.getId() + "/edit"));
        assertTrue(response.contains("value=\"Grimethorpe 1\""));
        assertTrue(response.contains("value=\"4\""));
    }
}
