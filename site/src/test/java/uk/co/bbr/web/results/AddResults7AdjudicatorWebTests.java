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
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.dao.ContestAdjudicatorDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestEventTestPieceDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:results-add-results-adjudicator-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddResults7AdjudicatorWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private ContestService contestService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestTypeService contestTypeService;
    @Autowired private PieceService pieceService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupData() throws AuthenticationFailedException {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao davidRead = this.personService.create("Read", "David");
        PersonDao davidRoberts = this.personService.create("Roberts", "David");

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 15));
        this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 16));
        ContestEventDao event17 = this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 17));
        this.contestEventService.addAdjudicator(event17, davidRoberts);
        ContestEventDao event18 = this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 18));
        this.contestEventService.addAdjudicator(event18, davidRead);
        ContestEventDao event19 = this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 19));

        logoutTestUser();
    }

    @Test
    void testEventAdjudicatorStageGetWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/add-results/7/yorkshire-area/2000-03-15", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Add Contest Results - Brass Band Results</title>"));

        assertTrue(response.contains("<h2>Add Contest Results</h2>"));
        assertTrue(response.contains(">Contest:<"));
        assertTrue(response.contains(">Event Date:<"));
        assertTrue(response.contains(">Contest Type:<"));
        assertTrue(response.contains(">Test Piece:<"));
        assertTrue(response.contains(">Adjudicators:<"));
    }

    @Test
    void testAddAdjudicatorWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("adjudicatorName", "David Read");
        map.add("adjudicatorSlug", "david-read");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/7/yorkshire-area/2000-03-15", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(2000,3,15));
        assertTrue(fetchedContestEvent.isPresent());

        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(fetchedContestEvent.get());
        assertEquals(1, adjudicators.size());
        assertEquals("david-read", adjudicators.get(0).getAdjudicator().getSlug());
        assertEquals("David Read", adjudicators.get(0).getAdjudicator().getName());
        assertEquals(fetchedContestEvent.get().getId(), adjudicators.get(0).getContestEvent().getId());
    }

    @Test
    void testBlankAdjudicatorWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("testPieceName", " ");
        map.add("testPieceSlug", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/7/yorkshire-area/2000-03-16", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(2000,3,16));
        assertTrue(fetchedContestEvent.isPresent());

        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(fetchedContestEvent.get());
        assertEquals(0, adjudicators.size());
    }

    @Test
    void testAddAdditionalAdjudicatorWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("adjudicatorName", "David Read");
        map.add("adjudicatorSlug", "david-read");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/7/yorkshire-area/2000-03-17", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(2000,3,17));
        assertTrue(fetchedContestEvent.isPresent());

        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(fetchedContestEvent.get());
        assertEquals(2, adjudicators.size());
        assertEquals("david-read", adjudicators.get(0).getAdjudicator().getSlug());
        assertEquals("David Read", adjudicators.get(0).getAdjudicator().getName());
        assertEquals("david-roberts", adjudicators.get(1).getAdjudicator().getSlug());
        assertEquals("David Roberts", adjudicators.get(1).getAdjudicator().getName());
        assertEquals(fetchedContestEvent.get().getId(), adjudicators.get(0).getContestEvent().getId());
        assertEquals(fetchedContestEvent.get().getId(), adjudicators.get(1).getContestEvent().getId());
    }

    @Test
    void testAddRepeatedIdenticalAdjudicatorWorksAsExpected() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("adjudicatorName", "David Read");
        map.add("adjudicatorSlug", "david-read");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/7/yorkshire-area/2000-03-18", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(2000,3,18));
        assertTrue(fetchedContestEvent.isPresent());

        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(fetchedContestEvent.get());
        assertEquals(1, adjudicators.size());
        assertEquals("david-read", adjudicators.get(0).getAdjudicator().getSlug());
        assertEquals("David Read", adjudicators.get(0).getAdjudicator().getName());
        assertEquals(fetchedContestEvent.get().getId(), adjudicators.get(0).getContestEvent().getId());
    }

    @Test
    void testInvalidAdjudicatorFailsAsExpected() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("adjudicatorName", "Not a real adjudicator");
        map.add("adjudicatorSlug", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/7/yorkshire-area/2000-03-19", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(2000,3,19));
        assertTrue(fetchedContestEvent.isPresent());

        List<ContestAdjudicatorDao> adjudicators = this.contestEventService.fetchAdjudicators(fetchedContestEvent.get());
        assertEquals(0, adjudicators.size());
    }
}
