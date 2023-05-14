package uk.co.bbr.web.people;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=people-bands-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:people-bands-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PeopleBandsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PersonService personService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupPeople() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.personService.create("Childs", "David");
        this.personService.create("Childs", "Nick");
        this.personService.create("Childs", "Bob");
        this.personService.create("Roberts", "David");

        logoutTestUser();
    }

    @Test
    void testGetFullWinnersListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/BANDS", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Conductors of Multiple Bands - Brass Band Results</title>"));

        // TODO add some data and test for it
    }

    @Test
    void testGetWinnersListBefore1950WorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/BANDS/before/1950", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Conductors of Multiple Bands - Brass Band Results</title>"));

        // TODO add some data and test for it
    }

    @Test
    void testGetWinnersListAfter1950WorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/BANDS/after/1950", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Conductors of Multiple Bands - Brass Band Results</title>"));

        // TODO add some data and test for it
    }
}
