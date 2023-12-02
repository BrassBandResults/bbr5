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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueAliasService;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueAliasDao;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:venue-alias-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenueAliasWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private VenueService venueService;
    @Autowired private VenueAliasService venueAliasService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao georgeHall = this.venueService.create("George Hall");
        VenueAliasDao personPreviousName1 = new VenueAliasDao();
        personPreviousName1.setName("St George's Hall");
        this.venueAliasService.createAlias(georgeHall, personPreviousName1);
        VenueAliasDao personPreviousName2 = new VenueAliasDao();
        personPreviousName2.setName("George's Hall");
        this.venueAliasService.createAlias(georgeHall, personPreviousName2);

        this.venueService.create("The Sage");

        logoutTestUser();
    }

    @Test
    void testListAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/george-hall/edit-aliases", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>George Hall - Venue Aliases - Brass Band Results</title>"));
        assertTrue(response.contains(">George Hall</a>"));
        assertTrue(response.contains("Aliases<"));
    }

    @Test
    void testListAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/not-a-real-venue/edit-aliases", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAliasWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        Optional<VenueDao> noAliasPerson = this.venueService.fetchBySlug("the-sage");
        assertTrue(noAliasPerson.isPresent());

        List<VenueAliasDao> fetchedAliases1 = this.venueAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(0, fetchedAliases1.size());

        VenueAliasDao previousName = new VenueAliasDao();
        previousName.setName("Old Name To Delete");
        VenueAliasDao newAlias = this.venueAliasService.createAlias(noAliasPerson.get(), previousName);

        List<VenueAliasDao> fetchedAliases2 = this.venueAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(1, fetchedAliases2.size());

        logoutTestUser();

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/the-sage/edit-aliases/" + newAlias.getId() + "/delete", String.class);
        assertNotNull(response);

        List<VenueAliasDao> fetchedAliases3 = this.venueAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(0, fetchedAliases3.size());
    }

    @Test
    void testDeleteAliasWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/not-a-real-venue/edit-aliases/1/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAliasWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/george-hall/edit-aliases/999/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateAliasWorksSuccessfully() {
        // arrange
        Optional<VenueDao> band = this.venueService.fetchBySlug("george-hall");
        assertTrue(band.isPresent());
        List<VenueAliasDao> fetchedAliases1 = this.venueAliasService.findAllAliases(band.get());
        assertEquals(2, fetchedAliases1.size());
        long aliasId = fetchedAliases1.get(0).getId();
        assertEquals("St. George's Hall", fetchedAliases1.get(0).getName());
        assertEquals("George's Hall", fetchedAliases1.get(1).getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "New Alias");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/venues/george-hall/edit-aliases/add", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<VenueAliasDao> fetchedAliases2 = this.venueAliasService.findAllAliases(band.get());
        assertEquals(3, fetchedAliases2.size());
        assertEquals("St. George's Hall", fetchedAliases2.get(0).getName());
        assertEquals("George's Hall", fetchedAliases2.get(1).getName());
        assertEquals("New Alias", fetchedAliases2.get(2).getName());
    }

    @Test
    void testCreateAliasWithInvalidBandSlugFailsAsExpected() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "New Alias");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + this.port + "/venues/not-a-real-venue/edit-aliases/add", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

