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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:people-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PeopleListWebTests implements LoginMixin {

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
    void testGetPeopleListForMultiplePeopleWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>People - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>People with surnames starting with A</h2>"));
        assertTrue(response.contains("Showing 0 of 4 people."));

        assertFalse(response.contains("Childs, David"));
        assertFalse(response.contains("Childs, Nick"));
        assertFalse(response.contains("Childs, Bob"));
        assertFalse(response.contains("Roberts, David"));
    }

    @Test
    void testGetPeopleListForOnePersonWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/R", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>People - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>People with surnames starting with R</h2>"));
        assertTrue(response.contains("Showing 1 of 4 people."));

        assertFalse(response.contains("Childs, David"));
        assertFalse(response.contains("Childs, Nick"));
        assertFalse(response.contains("Childs, Bob"));
        assertTrue(response.contains("Roberts, David"));
    }

    @Test
    void testGetPeopleListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/C", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>People - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>People with surnames starting with C</h2>"));
        assertTrue(response.contains("Showing 3 of 4 people."));

        assertTrue(response.contains("Childs, David"));
        assertTrue(response.contains("Childs, Nick"));
        assertTrue(response.contains("Childs, Bob"));
        assertFalse(response.contains("Roberts, David"));
    }
}
