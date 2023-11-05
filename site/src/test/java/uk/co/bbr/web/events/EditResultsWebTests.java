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
        "spring.datasource.url=jdbc:h2:mem:events-edit-results-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EditResultsWebTests implements LoginMixin {

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
    void testEditResultsGetWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area 2");
        ContestEventDao yorkshireArea2010 = this.contestEventService.create(yorkshireArea, LocalDate.of(2010, 3, 2));
        BandDao blackDyke = this.bandService.create("Black Dyke 2");
        PersonDao bobChilds = this.personService.create("Childs 2", "Bob");
        ContestResultDao result1 = this.contestResultService.addResult(yorkshireArea2010, "1", blackDyke, bobChilds);
        BandDao hepworth = this.bandService.create("Hepworth 2");
        PersonDao nickChilds = this.personService.create("Childs 2", "Nick");
        ContestResultDao result2 = this.contestResultService.addResult(yorkshireArea2010, "2", hepworth, nickChilds);

        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area-2/2010-03-02/edit-results", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("<form action=\"/contests/yorkshire-area-2/2010-03-02/edit-results\""));
        assertTrue(response.contains("Hepworth 2"));
        assertTrue(response.contains("Black Dyke 2"));
        assertTrue(response.contains("value=\"1\""));
        assertTrue(response.contains("value=\"2\""));
    }
}
