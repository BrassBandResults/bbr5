package uk.co.bbr.web.contests;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
                                "spring.datasource.url=jdbc:h2:mem:contests-contest-create-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestCreateWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestTypeService contestTypeService;
    @Autowired private RegionService regionService;
    @Autowired private SectionService sectionService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testCreateContestPageWorksSuccessfully() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/create/contest", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Create Contest"));
        assertTrue(response.contains("<form action = \"/create/contest\""));
    }

    @Test
    void testSubmitCreateContestPageSucceeds() throws AuthenticationFailedException {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();
        Optional<RegionDao> region = this.regionService.fetchBySlug("yorkshire");
        assertTrue(region.isPresent());
        Optional<SectionDao> section = this.sectionService.fetchBySlug("first");
        assertTrue(section.isPresent());

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        this.contestGroupService.create("Yorkshire Contests 1");
        this.contestService.create("National Finals 1");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Yorkshire Area 1");
        map.add("contestGroupName", "Yorkshire Contests 1");
        map.add("contestGroupSlug", "yorkshire-contests-1");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("region", String.valueOf(region.get().getId()));
        map.add("section", String.valueOf(section.get().getId()));
        map.add("ordering", "10");
        map.add("description", "Contest description");
        map.add("notes", "Contest notes");
        map.add("extinct", "true");
        map.add("excludeFromGroupResults", "true");
        map.add("allEventsAdded", "true");
        map.add("preventFutureBands", "true");
        map.add("repeatPeriod", "14");
        map.add("qualifiesForName", "National Finals 1");
        map.add("qualifiesForSlug", "national-finals-1");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/contest", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestDao> fetchedContest = this.contestService.fetchBySlug("yorkshire-area-1");
        assertTrue(fetchedContest.isPresent());
        assertEquals("Yorkshire Area 1", fetchedContest.get().getName());
        assertEquals("YORKSHIRE-CONTESTS-1", fetchedContest.get().getContestGroup().getSlug());
        assertEquals(contestType.getName(), fetchedContest.get().getDefaultContestType().getName());
        assertEquals("Yorkshire", fetchedContest.get().getRegion().getName());
        assertEquals("first", fetchedContest.get().getSection().getSlug());
        assertEquals(10, fetchedContest.get().getOrdering());
        assertEquals("Contest description", fetchedContest.get().getDescription());
        assertEquals("Contest notes", fetchedContest.get().getNotes());
        assertTrue(fetchedContest.get().isExtinct());
        assertTrue(fetchedContest.get().isExcludeFromGroupResults());
        assertTrue(fetchedContest.get().isAllEventsAdded());
        assertTrue(fetchedContest.get().isPreventFutureBands());
        assertEquals(14, fetchedContest.get().getRepeatPeriod());
        assertEquals("national-finals-1", fetchedContest.get().getQualifiesFor().getSlug());
    }

    @Test
    void testSubmitCreateContestPageFailsBecauseNameIsRequired() throws AuthenticationFailedException {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();
        Optional<RegionDao> region = this.regionService.fetchBySlug("yorkshire");
        assertTrue(region.isPresent());
        Optional<SectionDao> section = this.sectionService.fetchBySlug("first");
        assertTrue(section.isPresent());

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        this.contestGroupService.create("Yorkshire Contests 2");
        this.contestService.create("National Finals 2");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "   ");
        map.add("contestGroupName", "Yorkshire Contests 2");
        map.add("contestGroupSlug", "yorkshire-contests-2");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("region", String.valueOf(region.get().getId()));
        map.add("section", String.valueOf(section.get().getId()));
        map.add("ordering", "10");
        map.add("description", "Contest description");
        map.add("notes", "Contest notes");
        map.add("extinct", "true");
        map.add("excludeFromGroupResults", "true");
        map.add("allEventsAdded", "true");
        map.add("preventFutureBands", "true");
        map.add("repeatPeriod", "14");
        map.add("qualifiesForName", "National Finals 2");
        map.add("qualifiesForSlug", "national-finals-2");
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

    @Test
    void testSubmitCreateContestWithNoGroupSucceeds() throws AuthenticationFailedException {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();
        Optional<RegionDao> region = this.regionService.fetchBySlug("yorkshire");
        assertTrue(region.isPresent());
        Optional<SectionDao> section = this.sectionService.fetchBySlug("first");
        assertTrue(section.isPresent());

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        this.contestService.create("National Finals 3");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Yorkshire Area 3");
        map.add("contestGroupName", "");
        map.add("contestGroupSlug", "");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("region", String.valueOf(region.get().getId()));
        map.add("section", String.valueOf(section.get().getId()));
        map.add("ordering", "10");
        map.add("description", "Contest description");
        map.add("notes", "Contest notes");
        map.add("extinct", "true");
        map.add("excludeFromGroupResults", "true");
        map.add("allEventsAdded", "true");
        map.add("preventFutureBands", "true");
        map.add("repeatPeriod", "14");
        map.add("qualifiesForName", "National Finals 3");
        map.add("qualifiesForSlug", "national-finals-3");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/contest", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestDao> fetchedContest = this.contestService.fetchBySlug("yorkshire-area-3");
        assertTrue(fetchedContest.isPresent());
        assertEquals("Yorkshire Area 3", fetchedContest.get().getName());
        assertNull(fetchedContest.get().getContestGroup());
        assertEquals(contestType.getName(), fetchedContest.get().getDefaultContestType().getName());
        assertEquals("Yorkshire", fetchedContest.get().getRegion().getName());
        assertEquals("first", fetchedContest.get().getSection().getSlug());
        assertEquals(10, fetchedContest.get().getOrdering());
        assertEquals("Contest description", fetchedContest.get().getDescription());
        assertEquals("Contest notes", fetchedContest.get().getNotes());
        assertTrue(fetchedContest.get().isExtinct());
        assertTrue(fetchedContest.get().isExcludeFromGroupResults());
        assertTrue(fetchedContest.get().isAllEventsAdded());
        assertTrue(fetchedContest.get().isPreventFutureBands());
        assertEquals(14, fetchedContest.get().getRepeatPeriod());
        assertEquals("national-finals-3", fetchedContest.get().getQualifiesFor().getSlug());
    }

    @Test
    void testSubmitCreateContestWithNoQualifiesForSucceeds() throws AuthenticationFailedException {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        ContestTypeDao contestType = this.contestTypeService.fetchDefaultContestType();
        Optional<RegionDao> region = this.regionService.fetchBySlug("yorkshire");
        assertTrue(region.isPresent());
        Optional<SectionDao> section = this.sectionService.fetchBySlug("first");
        assertTrue(section.isPresent());

        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);
        this.contestGroupService.create("Yorkshire Contests 4");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Yorkshire Area 4");
        map.add("contestGroupName", "Yorkshire Contests 4");
        map.add("contestGroupSlug", "yorkshire-contests-4");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("region", String.valueOf(region.get().getId()));
        map.add("section", String.valueOf(section.get().getId()));
        map.add("ordering", "10");
        map.add("description", "Contest description");
        map.add("notes", "Contest notes");
        map.add("extinct", "true");
        map.add("excludeFromGroupResults", "true");
        map.add("allEventsAdded", "true");
        map.add("preventFutureBands", "true");
        map.add("repeatPeriod", "14");
        map.add("qualifiesForName", "");
        map.add("qualifiesForSlug", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/contest", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestDao> fetchedContest = this.contestService.fetchBySlug("yorkshire-area-4");
        assertTrue(fetchedContest.isPresent());
        assertEquals("Yorkshire Area 4", fetchedContest.get().getName());
        assertEquals("YORKSHIRE-CONTESTS-4", fetchedContest.get().getContestGroup().getSlug());
        assertEquals(contestType.getName(), fetchedContest.get().getDefaultContestType().getName());
        assertEquals("Yorkshire", fetchedContest.get().getRegion().getName());
        assertEquals("first", fetchedContest.get().getSection().getSlug());
        assertEquals(10, fetchedContest.get().getOrdering());
        assertEquals("Contest description", fetchedContest.get().getDescription());
        assertEquals("Contest notes", fetchedContest.get().getNotes());
        assertTrue(fetchedContest.get().isExtinct());
        assertTrue(fetchedContest.get().isExcludeFromGroupResults());
        assertTrue(fetchedContest.get().isAllEventsAdded());
        assertTrue(fetchedContest.get().isPreventFutureBands());
        assertEquals(14, fetchedContest.get().getRepeatPeriod());
        assertNull(fetchedContest.get().getQualifiesFor());
    }

}

