package uk.co.bbr.web.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:contests-event-edit-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventEditWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private VenueService venueService;
    @Autowired private ContestTypeService contestTypeService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestDao northWestArea = this.contestService.create("North West Area");

        VenueDao testVenue = this.venueService.create("Test Venue");

        this.contestEventService.create(yorkshireArea, LocalDate.of(2010, 3, 12));
        this.contestEventService.create(yorkshireArea, LocalDate.of(2011, 3, 12));
        this.contestEventService.create(northWestArea, LocalDate.of(2010, 3, 13));
        ContestEventDao event = this.contestEventService.create(northWestArea, LocalDate.of(2012, 4, 14));
        event.setVenue(testVenue);
        this.contestEventService.update(event);


        logoutTestUser();
    }

    @Test
    void testEditEventPageWorksSuccessfully() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/2011-03-12/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Yorkshire Area"));
        assertTrue(response.contains("<form action=\"/contests/yorkshire-area/2011-03-12/edit\""));
        assertTrue(response.contains("value=\"Yorkshire Area\""));
    }

    @Test
    void testEditEventPageGetFailsWhereSlugIsNotFound() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/not-a-slug-that-exists/2010-03-12/edit", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSubmitEditEventPageSucceeds() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Yorkshire Area 1");
        map.add("eventDate", "2011-04-15");
        map.add("dateResolution", "D");
        map.add("notes", "Contest notes");
        map.add("noContest", "true");
        map.add("venueName", "Test Venue");
        map.add("venueSlug", "test-venue");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/contests/yorkshire-area/2010-03-12/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestEventDao> fetchedEvent = this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(2011,4,15));
        assertTrue(fetchedEvent.isPresent());
        assertEquals("Yorkshire Area 1", fetchedEvent.get().getName());
        assertEquals(LocalDate.of(2011,4,15), fetchedEvent.get().getEventDate());
        assertEquals(ContestEventDateResolution.EXACT_DATE, fetchedEvent.get().getEventDateResolution());
        assertEquals("Contest notes", fetchedEvent.get().getNotes());
        assertTrue(fetchedEvent.get().isNoContest());
        assertEquals("Test Venue", fetchedEvent.get().getVenue().getName());
        assertEquals(contestType.getName(), fetchedEvent.get().getContestType().getName());
    }

    @Test
    void testSubmitEditEventPageFailsBecauseNameIsRequired() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "");
        map.add("eventDate", "2011-04-15");
        map.add("dateResolution", "D");
        map.add("notes", "Contest notes");
        map.add("noContest", "true");
        map.add("venueName", "Test Venue");
        map.add("venueSlug", "test-venue");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/contests/north-west-area/2010-03-13/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A contest event must have a name"));
    }


    @Test
    void testSubmitEditContestPageFailsWhereSlugIsNotFound() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Scottish Area");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + port + "/contests/no-a-valid-slug/2010-03-13/edit", request, String.class));

        // assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSubmitEditContestPageWithBlankVenueClearsVenue() {
        Optional<ContestEventDao> fetchedEventBefore = this.contestEventService.fetchEvent("north-west-area", LocalDate.of(2012,4,14));
        assertTrue(fetchedEventBefore.isPresent());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Contest Name");
        map.add("eventDate", "2012-04-14");
        map.add("dateResolution", "D");
        map.add("notes", "Contest notes");
        map.add("noContest", "true");
        map.add("venueName", "");
        map.add("venueSlug", "");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/contests/north-west-area/2012-04-14/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestEventDao> fetchedEvent = this.contestEventService.fetchEvent("north-west-area", LocalDate.of(2012,4,14));
        assertTrue(fetchedEvent.isPresent());

        assertNull(fetchedEvent.get().getVenue());

        assertEquals("Contest Name", fetchedEvent.get().getName());
        assertEquals(LocalDate.of(2012,4,14), fetchedEvent.get().getEventDate());
        assertEquals(ContestEventDateResolution.EXACT_DATE, fetchedEvent.get().getEventDateResolution());
        assertEquals("Contest notes", fetchedEvent.get().getNotes());
        assertTrue(fetchedEvent.get().isNoContest());
        assertEquals(contestType.getName(), fetchedEvent.get().getContestType().getName());

    }

}

