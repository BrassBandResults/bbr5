package uk.co.bbr.web.venues;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:venues-venue-create-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenueCreateWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private VenueService venueService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testCreateVenuePageWorksSuccessfullyForLoggedInUser() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/create/venue", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Create Venue"));
        assertTrue(response.contains("<form action = \"/create/venue\""));
    }

    @Test
    void testSubmitCreateVenuePageSucceedsForLoggedInMember() throws AuthenticationFailedException {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        VenueDao composer = this.venueService.create("Parent Venue");

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "  New       Venue  ");
        map.add("region", "5");
        map.add("latitude", "  1.1  ");
        map.add("longitude", "    2.2    ");
        map.add("notes", "    Here are some notes.   ");
        map.add("parentVenueName", "Parent Venue");
        map.add("parentVenueSlug", "parent-venue");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/venue", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<VenueDao> fetchedVenue = this.venueService.fetchBySlug("new-venue");
        assertTrue(fetchedVenue.isPresent());
        assertEquals("New Venue", fetchedVenue.get().getName());
        assertEquals("London and Southern Counties", fetchedVenue.get().getRegion().getName());
        assertEquals("2.2", fetchedVenue.get().getLongitude());
        assertEquals("1.1", fetchedVenue.get().getLatitude());
        assertEquals("Here are some notes.", fetchedVenue.get().getNotes());
        assertEquals("Parent Venue", fetchedVenue.get().getParent().getName());
        assertEquals("parent-venue", fetchedVenue.get().getParent().getSlug());
    }

    @Test
    void testSubmitCreateVenuePageFailsBecauseNameIsRequired() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "");
        map.add("region", "5");
        map.add("latitude", "  1.1  ");
        map.add("longitude", "    2.2    ");
        map.add("notes", "    Here are some notes.   ");
        map.add("parentVenueName", "Parent Venue");
        map.add("parentVenueSlug", "parent-venue");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/venue", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A venue must have a name"));
    }
}

