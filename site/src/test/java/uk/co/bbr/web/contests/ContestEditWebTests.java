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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.ContestTypeService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.contests.dao.ContestTypeDao;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
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
        "spring.datasource.url=jdbc:h2:mem:contests-edit-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContestEditWebTests implements LoginMixin {

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
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.contestService.create("Yorkshire Area");
        this.contestService.create("North West Area");
        this.contestService.create("National Finals");
        this.contestGroupService.create("Yorkshire Contests");

        logoutTestUser();
    }

    @Test
    void testEditContestPageWorksSuccessfully() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/yorkshire-area/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Yorkshire Area"));
        assertTrue(response.contains("<form action=\"/contests/yorkshire-area/edit\""));
        assertTrue(response.contains("value=\"Yorkshire Area\""));
    }

    @Test
    void testEditContestPageGetFailsWhereSlugIsNotFound() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/contests/not-a-slug-that-exists/edit", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSubmitEditContestPageSucceeds() {
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

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Yorkshire Area 1");
        map.add("contestGroupName", "Yorkshire Contests");
        map.add("contestGroupSlug", "yorkshire-contests");
        map.add("contestType", String.valueOf(contestType.getId()));
        map.add("region", String.valueOf(region.get().getId()));
        map.add("section", String.valueOf(section.get().getId()));
        map.add("ordering", "10");
        map.add("description", "Contest description");
        map.add("notes", "Contest notes");
        map.add("extinct", "true");
        map.add("excludeFromGroupResults", "true");
        map.add("qualifiesForName", "National Finals");
        map.add("qualifiesForSlug", "national-finals");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/contests/yorkshire-area/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<ContestDao> fetchedContest = this.contestService.fetchBySlug("yorkshire-area");
        assertTrue(fetchedContest.isPresent());
        assertEquals("Yorkshire Area 1", fetchedContest.get().getName());
        assertEquals("YORKSHIRE-CONTESTS", fetchedContest.get().getContestGroup().getSlug());
        assertEquals(contestType.getName(), fetchedContest.get().getDefaultContestType().getName());
        assertEquals("Yorkshire", fetchedContest.get().getRegion().getName());
        assertEquals("first", fetchedContest.get().getSection().getSlug());
        assertEquals(10, fetchedContest.get().getOrdering());
        assertEquals("Contest description", fetchedContest.get().getDescription());
        assertEquals("Contest notes", fetchedContest.get().getNotes());
        assertTrue(fetchedContest.get().isExtinct());
        assertTrue(fetchedContest.get().isExcludeFromGroupResults());
        assertEquals("national-finals", fetchedContest.get().getQualifiesFor().getSlug());
    }

    @Test
    void testSubmitEditContestPageFailsBecauseNameIsRequired() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/contests/north-west-area/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A contest must have a name"));
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
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + port + "/people/no-a-valid-slug/edit", request, String.class));

        // assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

