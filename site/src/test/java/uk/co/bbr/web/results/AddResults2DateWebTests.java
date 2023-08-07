package uk.co.bbr.web.results;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.types.ContestEventDateResolution;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:results-add-results-date-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddResults2DateWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private ContestService contestService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService contestResultService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private ContestTypeService contestTypeService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupData() throws AuthenticationFailedException {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        Optional<ContestTypeDao> ownChoiceType = this.contestTypeService.fetchBySlug("own-choice-test-piece-contest");
        assertTrue(ownChoiceType.isPresent());
        Optional<ContestTypeDao> entertainmentsType = this.contestTypeService.fetchBySlug("entertainments-contest");
        assertTrue(entertainmentsType.isPresent());

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        yorkshireArea.setDefaultContestType(ownChoiceType.get());
        this.contestService.update(yorkshireArea);

        ContestDao spennymoor = this.contestService.create("Spennymoor");
        spennymoor.setDefaultContestType(entertainmentsType.get());
        this.contestService.update(spennymoor);

        logoutTestUser();
    }

    @Test
    void testEventDateStageGetWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/add-results/2/yorkshire-area", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Add Contest Results - Brass Band Results</title>"));

        assertTrue(response.contains("<h2>Add Contest Results</h2>"));
        assertTrue(response.contains(">Contest:<"));
        assertTrue(response.contains(">Event Date:<"));
    }

    @Test
    void testCreateNewEventWithExactDateWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("eventDate", "23/03/1990");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/2/yorkshire-area", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/add-results/3/yorkshire-area/1990-03-23"));

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(1990,3,23));
        assertTrue(fetchedContestEvent.isPresent());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getName());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getContest().getName());
        assertEquals(LocalDate.of(1990,3,23), fetchedContestEvent.get().getEventDate());
        assertEquals(ContestEventDateResolution.EXACT_DATE, fetchedContestEvent.get().getEventDateResolution());
    }

    @Test
    void testCreateNewEventWithExactDateDefaultsInContestTypeSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("eventDate", "23/04/1991");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/2/yorkshire-area", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/add-results/3/yorkshire-area/1991-04-23"));

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(1991,4,23));
        assertTrue(fetchedContestEvent.isPresent());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getName());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getContest().getName());
        assertEquals(LocalDate.of(1991,4,23), fetchedContestEvent.get().getEventDate());
        assertEquals("own-choice-test-piece-contest", fetchedContestEvent.get().getContestType().getSlug());
        assertEquals(ContestEventDateResolution.EXACT_DATE, fetchedContestEvent.get().getEventDateResolution());
    }

    @Test
    void testCreateNewEventWithMonthResolutionWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("eventDate", "3/1992");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/2/yorkshire-area", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/add-results/3/yorkshire-area/1992-03-01"));

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(1992,3,1));
        assertTrue(fetchedContestEvent.isPresent());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getName());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getContest().getName());
        assertEquals(LocalDate.of(1992,3,1), fetchedContestEvent.get().getEventDate());
        assertEquals(ContestEventDateResolution.MONTH_AND_YEAR, fetchedContestEvent.get().getEventDateResolution());
    }

    @Test
    void testCreateNewEventWithMonthResolutionDefaultsInContestTypeSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("eventDate", "5/1993");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/2/spennymoor", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/add-results/3/spennymoor/1993-05-01"));

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("spennymoor", LocalDate.of(1993,5,1));
        assertTrue(fetchedContestEvent.isPresent());
        assertEquals("Spennymoor", fetchedContestEvent.get().getName());
        assertEquals("Spennymoor", fetchedContestEvent.get().getContest().getName());
        assertEquals("entertainments-contest", fetchedContestEvent.get().getContestType().getSlug());
        assertEquals(LocalDate.of(1993,5,1), fetchedContestEvent.get().getEventDate());
        assertEquals(ContestEventDateResolution.MONTH_AND_YEAR, fetchedContestEvent.get().getEventDateResolution());
    }

    @Test
    void testCreateNewEventWithMonthResolutionAndLeadingZeroWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("eventDate", "03/1994");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/2/yorkshire-area", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/add-results/3/yorkshire-area/1994-03-01"));

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(1994,3,1));
        assertTrue(fetchedContestEvent.isPresent());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getName());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getContest().getName());
        assertEquals(LocalDate.of(1994,3,1), fetchedContestEvent.get().getEventDate());
        assertEquals(ContestEventDateResolution.MONTH_AND_YEAR, fetchedContestEvent.get().getEventDateResolution());
    }

    @Test
    void testCreateNewEventWithYearResolutionWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("eventDate", "1995");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/2/yorkshire-area", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/add-results/3/yorkshire-area/1995-01-01"));

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(1995,1,1));
        assertTrue(fetchedContestEvent.isPresent());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getName());
        assertEquals("Yorkshire Area", fetchedContestEvent.get().getContest().getName());
        assertEquals(LocalDate.of(1995,1,1), fetchedContestEvent.get().getEventDate());
        assertEquals(ContestEventDateResolution.YEAR, fetchedContestEvent.get().getEventDateResolution());
    }

    @Test
    void testCreateNewEventWithinAMonthFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        Optional<ContestDao> yorkshireArea = this.contestService.fetchBySlug("yorkshire-area");
        ContestEventDao yorkshireArea1996 = this.contestEventService.create(yorkshireArea.get(), LocalDate.of(1996, 3, 3));
        BandDao band = this.bandService.create("Band");
        PersonDao conductor = this.personService.create("Condutor", "Mr");
        this.contestResultService.addResult(yorkshireArea1996, "1", band, conductor);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("eventDate", "01/04/1996");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/2/yorkshire-area", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/contests/yorkshire-area/1996-03-03"));

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(1995,4,1));
        assertFalse(fetchedContestEvent.isPresent());
    }
}
