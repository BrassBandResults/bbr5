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
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ResultPositionType;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
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
        "spring.datasource.url=jdbc:h2:mem:results-add-results-bands-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddResults6BandsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private ContestService contestService;
    @Autowired private PersonService personService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ResultService resultService;
    @Autowired private VenueService venueService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupData() throws AuthenticationFailedException {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 1));
        this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 2));

        this.personService.create("Roberts", "David");
        this.personService.create("Childs", "Nick");
        this.bandService.create("Rothwell Temperance Band");
        this.bandService.create("Black Dyke");

        logoutTestUser();
    }

    @Test
    void testEventBandsStageGetWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/add-results/6/yorkshire-area/2000-03-01", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Add Contest Results - Brass Band Results</title>"));

        assertTrue(response.contains("<h2>Add Contest Results</h2>"));
        assertTrue(response.contains(">Contest:<"));
        assertTrue(response.contains(">Event Date:<"));
        assertTrue(response.contains(">Contest Type:<"));
        assertTrue(response.contains(">Test Piece:<"));
        assertTrue(response.contains(">Venue:<"));
        assertTrue(response.contains(">Results:<"));
    }

    @Test
    void testAddSingleResultThatMatchesWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("resultBlock", "1. Rothwell Temperance Band, David Roberts, 5");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/6/yorkshire-area/2000-03-01", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/add-results/7/yorkshire-area/2000-03-01"));
        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(2000,3,1));
        assertTrue(fetchedContestEvent.isPresent());

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(fetchedContestEvent.get());
        assertEquals(1, eventResults.size());
        assertEquals(1, eventResults.get(0).getPosition());
        assertEquals("1", eventResults.get(0).getPositionDisplay());
        assertEquals(ResultPositionType.RESULT, eventResults.get(0).getResultPositionType());
        assertEquals("rothwell-temperance-band", eventResults.get(0).getBand().getSlug());
        assertEquals("Rothwell Temperance Band", eventResults.get(0).getBand().getName());
        assertEquals("Rothwell Temperance Band", eventResults.get(0).getBandName());
        assertEquals("David Roberts", eventResults.get(0).getOriginalConductorName());
        assertEquals("David Roberts", eventResults.get(0).getConductor().getCombinedName());
        assertEquals("david-roberts", eventResults.get(0).getConductor().getSlug());
        assertEquals("Roberts", eventResults.get(0).getConductor().getSurname());
        assertEquals("David", eventResults.get(0).getConductor().getFirstNames());
        assertEquals(5, eventResults.get(0).getDraw());
    }

    @Test
    void testAddMultipleResultsThatMatchWorksSuccessfully() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("resultBlock", """
            1. Rothwell Temperance Band, David Roberts, 5
            2. Black Dyke, Nick Childs, 3
            """);
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/add-results/6/yorkshire-area/2000-03-02", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/add-results/7/yorkshire-area/2000-03-02"));
        Optional<ContestEventDao> fetchedContestEvent =  this.contestEventService.fetchEvent("yorkshire-area", LocalDate.of(2000,3,2));
        assertTrue(fetchedContestEvent.isPresent());

        List<ContestResultDao> eventResults = this.resultService.fetchForEvent(fetchedContestEvent.get());
        assertEquals(2, eventResults.size());

        assertEquals(1, eventResults.get(0).getPosition());
        assertEquals("1", eventResults.get(0).getPositionDisplay());
        assertEquals(ResultPositionType.RESULT, eventResults.get(0).getResultPositionType());
        assertEquals("rothwell-temperance-band", eventResults.get(0).getBand().getSlug());
        assertEquals("Rothwell Temperance Band", eventResults.get(0).getBand().getName());
        assertEquals("Rothwell Temperance Band", eventResults.get(0).getBandName());
        assertEquals("David Roberts", eventResults.get(0).getOriginalConductorName());
        assertEquals("David Roberts", eventResults.get(0).getConductor().getCombinedName());
        assertEquals("david-roberts", eventResults.get(0).getConductor().getSlug());
        assertEquals("Roberts", eventResults.get(0).getConductor().getSurname());
        assertEquals("David", eventResults.get(0).getConductor().getFirstNames());
        assertEquals(5, eventResults.get(0).getDraw());

        assertEquals(2, eventResults.get(1).getPosition());
        assertEquals("2", eventResults.get(1).getPositionDisplay());
        assertEquals(ResultPositionType.RESULT, eventResults.get(1).getResultPositionType());
        assertEquals("black-dyke", eventResults.get(1).getBand().getSlug());
        assertEquals("Black Dyke", eventResults.get(1).getBand().getName());
        assertEquals("Black Dyke", eventResults.get(1).getBandName());
        assertEquals("Nick Childs", eventResults.get(1).getOriginalConductorName());
        assertEquals("Nick Childs", eventResults.get(1).getConductor().getCombinedName());
        assertEquals("nick-childs", eventResults.get(1).getConductor().getSlug());
        assertEquals("Childs", eventResults.get(1).getConductor().getSurname());
        assertEquals("Nick", eventResults.get(1).getConductor().getFirstNames());
        assertEquals(3, eventResults.get(1).getDraw());
    }
}

