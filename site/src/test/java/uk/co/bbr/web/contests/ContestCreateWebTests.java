package uk.co.bbr.web.contests;

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
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
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
        "spring.datasource.url=jdbc:h2:mem:contests-contest-create-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestCreateWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private ContestService contestService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private SectionService sectionService;
    @Autowired private ContestTypeService contestTypeService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testCreateContestPageWorksSuccessfullyForLoggedInUser() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/create/contest", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Create Contest"));
        assertTrue(response.contains("<form action = \"/create/contest\""));
        assertTrue(response.contains("selected=\"selected\">Unknown</option>"));
    }

    @Test
    void testSubmitCreateContestPageSucceedsForLoggedInMember() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        this.contestGroupService.create("Example Group");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        Optional<RegionDao> northWest = this.regionService.fetchBySlug("north-west");
        assertTrue(northWest.isPresent());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();

        Optional<SectionDao> firstSection = this.sectionService.fetchBySlug("first");
        assertTrue(firstSection.isPresent());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "   Example    Contest   ");
        map.add("contestGroup", "example-group");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("region", String.valueOf(northWest.get().getId()));
        map.add("section", String.valueOf(firstSection.get().getId()));
        map.add("ordering", "3");
        map.add("repeatPeriod", "12");
        map.add("description", "  This is the description  ");
        map.add("notes", "  These are the notes  ");
        map.add("extinct", "true");
        map.add("excludeFromGroupResults", "true");
        map.add("allEventsAdded", "true");
        map.add("preventFutureBands", "false");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/contest", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/contests"));

        Optional<ContestDao> fetchedContest = this.contestService.fetchBySlug("example-contest");
        assertTrue(fetchedContest.isPresent());
        assertEquals("Example Contest", fetchedContest.get().getName());
        assertEquals("Example Group", fetchedContest.get().getContestGroup().getName());
        assertEquals("Test Piece Contest", fetchedContest.get().getDefaultContestType().getName());
        assertEquals("North West", fetchedContest.get().getRegion().getName());
        assertEquals("First", fetchedContest.get().getSection().getName());
        assertEquals(3, fetchedContest.get().getOrdering());
        assertEquals(12, fetchedContest.get().getRepeatPeriod());
        assertTrue(fetchedContest.get().isExtinct());
        assertTrue(fetchedContest.get().isExcludeFromGroupResults());
        assertTrue(fetchedContest.get().isAllEventsAdded());
        assertFalse(fetchedContest.get().isPreventFutureBands());
        assertEquals("This is the description", fetchedContest.get().getDescription());
        assertEquals("These are the notes", fetchedContest.get().getNotes());
    }

    @Test
    void testSubmitCreateContestPageFailsBecauseNameIsRequired() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        Optional<RegionDao> northWest = this.regionService.fetchBySlug("north-west");
        assertTrue(northWest.isPresent());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();

        Optional<SectionDao> firstSection = this.sectionService.fetchBySlug("first");
        assertTrue(firstSection.isPresent());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "");
        map.add("contestGroup", "example-group");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("region", String.valueOf(northWest.get().getId()));
        map.add("section", String.valueOf(firstSection.get().getId()));
        map.add("ordering", "3");
        map.add("repeatPeriod", "12");
        map.add("description", "  This is the description  ");
        map.add("notes", "  These are the notes  ");
        map.add("extinct", "true");
        map.add("excludeFromGroupResults", "true");
        map.add("allEventsAdded", "true");
        map.add("preventFutureBands", "true");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/contest", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A contest must have a name"));
    }
}

