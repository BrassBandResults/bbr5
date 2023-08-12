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
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:venues-venue-edit-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenueEditWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private VenueService venueService;
    @Autowired private RegionService regionService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testEditVenuePageWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        this.venueService.create("Test Venue 1");

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/test-venue-1/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Test Venue 1"));
        assertTrue(response.contains("<form action = \"/venues/test-venue-1/edit\""));
    }

    @Test
    void testEditVenuePageFailsWithInvalidSlug() throws AuthenticationFailedException {
        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/not-a-real-venue-slug/edit", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSubmitEditVenuePageSucceeds() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        VenueDao testVenue = this.venueService.create("Test Venue 2a");
        VenueDao composer = this.venueService.create("Parent Venue");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

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
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/venues/" + testVenue.getSlug() + "/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<VenueDao> fetchedVenue = this.venueService.fetchBySlug("test-venue-2a");
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
    void testSubmitEditVenuePageWithParentAndRegionSucceeds() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao parent1 = this.venueService.create("Parent Venue 2c old");
        this.venueService.create("Parent Venue 2c new");

        VenueDao testVenue = this.venueService.create("Test Venue 2c");
        Optional<RegionDao> yorkshire = this.regionService.fetchBySlug("yorkshire");
        assertTrue(yorkshire.isPresent());
        testVenue.setRegion(yorkshire.get());
        testVenue.setParent(parent1);
        VenueDao updatedVenue = this.venueService.update(testVenue);
        assertNotNull(updatedVenue.getRegion());
        assertNotNull(updatedVenue.getParent());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "  New       Venue  2c");
        map.add("region", "5");
        map.add("latitude", "  1.1  ");
        map.add("longitude", "    2.2    ");
        map.add("notes", "    Here are some notes.   ");
        map.add("parentVenueName", "Parent Venue 2c new");
        map.add("parentVenueSlug", "parent-venue-2c-new");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/venues/" + testVenue.getSlug() + "/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<VenueDao> fetchedVenue = this.venueService.fetchBySlug("test-venue-2c");
        assertTrue(fetchedVenue.isPresent());
        assertEquals("New Venue 2c", fetchedVenue.get().getName());
        assertEquals("London and Southern Counties", fetchedVenue.get().getRegion().getName());
        assertEquals("2.2", fetchedVenue.get().getLongitude());
        assertEquals("1.1", fetchedVenue.get().getLatitude());
        assertEquals("Here are some notes.", fetchedVenue.get().getNotes());
        assertEquals("Parent Venue 2c new", fetchedVenue.get().getParent().getName());
        assertEquals("parent-venue-2c-new", fetchedVenue.get().getParent().getSlug());
    }

    @Test
    void testSubmitEditVenuePageWithNoRegionSucceeds() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        VenueDao testVenue = this.venueService.create("Test Venue 2b");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "  New       Venue  2b");
        map.add("latitude", "  1.1  ");
        map.add("longitude", "    2.2    ");
        map.add("notes", "    Here are some notes.   ");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/venues/" + testVenue.getSlug() + "/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<VenueDao> fetchedVenue = this.venueService.fetchBySlug("test-venue-2b");
        assertTrue(fetchedVenue.isPresent());
        assertEquals("New Venue 2b", fetchedVenue.get().getName());
        assertNull(fetchedVenue.get().getRegion());
        assertEquals("2.2", fetchedVenue.get().getLongitude());
        assertEquals("1.1", fetchedVenue.get().getLatitude());
        assertEquals("Here are some notes.", fetchedVenue.get().getNotes());
        assertNull(fetchedVenue.get().getParent());
    }

    @Test
    void testSubmitEditVenuePageWithNoParentSucceeds() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        VenueDao testVenue = this.venueService.create("Test Venue 2");
        this.venueService.create("Parent Venue 2");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "  New       Venue   2");
        map.add("region", "5");
        map.add("latitude", "  1.1  ");
        map.add("longitude", "    2.2    ");
        map.add("notes", "    Here are some notes.   ");
        map.add("parentVenueName", "Parent Venue 2");
        map.add("parentVenueSlug", "parent-venue-2");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/venues/" + testVenue.getSlug() + "/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<VenueDao> fetchedVenue = this.venueService.fetchBySlug("test-venue-2");
        assertTrue(fetchedVenue.isPresent());
        assertEquals("New Venue 2", fetchedVenue.get().getName());
        assertEquals("London and Southern Counties", fetchedVenue.get().getRegion().getName());
        assertEquals("2.2", fetchedVenue.get().getLongitude());
        assertEquals("1.1", fetchedVenue.get().getLatitude());
        assertEquals("Here are some notes.", fetchedVenue.get().getNotes());
        assertEquals("Parent Venue 2", fetchedVenue.get().getParent().getName());
        assertEquals("parent-venue-2", fetchedVenue.get().getParent().getSlug());
    }

    @Test
    void testSubmitEditVenuePageFailsWithInvalidSlug() throws AuthenticationFailedException {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "  New       Venue  ");
        map.add("region", "5");
        map.add("latitude", "  1.1  ");
        map.add("longitude", "    2.2    ");
        map.add("notes", "    Here are some notes.   ");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + port + "/venues/not-a-valid-venue-slug/edit", request, String.class));

        // assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSubmitEditVenuePageFailsBecauseNameIsRequired() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        VenueDao testVenue = this.venueService.create("Test Venue 3");

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
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/venues/" + testVenue.getSlug() + "/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A venue must have a name"));
    }

    @Test
    void testEditVenueWithRegionWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        Optional<RegionDao> region = this.regionService.fetchBySlug("yorkshire");
        assertTrue(region.isPresent());
        VenueDao testVenue = this.venueService.create("Test Venue 4");
        testVenue.setRegion(region.get());
        this.venueService.update(testVenue);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/test-venue-4/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Test Venue 4"));
        assertTrue(response.contains("<form action = \"/venues/test-venue-4/edit\""));
        assertTrue(response.contains("Yorkshire"));
    }

    @Test
    void testEditVenueWithParentWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        VenueDao parent = this.venueService.create("Parent 5");
        VenueDao testVenue = this.venueService.create("Test Venue 5");
        testVenue.setParent(parent);
        this.venueService.update(testVenue);

        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/venues/test-venue-5/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Test Venue 5"));
        assertTrue(response.contains("<form action = \"/venues/test-venue-5/edit\""));
        assertTrue(response.contains("Parent"));
    }
}

