package uk.co.bbr.web.people;

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
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=person-edit-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:person-edit-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonEditWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private PersonService personService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.personService.create("Beckley", "Duncan");
        this.personService.create("Childs", "Robert");

        logoutTestUser();
    }

    @Test
    void testEditBandPageWorksSuccessfullyForLoggedInUser() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/duncan-beckley/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Duncan Beckley"));
        assertTrue(response.contains("<form action = \"/people/duncan-beckley/edit\""));
        assertTrue(response.contains("value=\"Duncan\""));
        assertTrue(response.contains("value=\"Beckley\""));
    }

    @Test
    void testEditBandPageGetFailsWhereSlugIsNotFound() {
        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit", String.class));

        // assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSubmitEditBandPageSucceedsForLoggedInMember() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("firstNames", "  Duncan  1  ");
        map.add("surname", " Beckley    4  ");
        map.add("suffix", "  Jnr  ");
        map.add("knownFor", "  Rothwell  ");
        map.add("startDate", "1990-01-01");
        map.add("endDate", "2000-02-02");
        map.add("notes", "  These are people notes  ");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/people/robert-childs/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/people/robert-childs"));

        Optional<PersonDao> fetchedPerson = this.personService.fetchBySlug("robert-childs");
        assertTrue(fetchedPerson.isPresent());
        assertEquals("Beckley 4", fetchedPerson.get().getSurname());
        assertEquals("Duncan 1", fetchedPerson.get().getFirstNames());
        assertEquals("Jnr", fetchedPerson.get().getSuffix());
        assertEquals("Rothwell", fetchedPerson.get().getKnownFor());
        assertEquals(LocalDate.of(1990, 1, 1), fetchedPerson.get().getStartDate());
        assertEquals(LocalDate.of(2000, 2, 2), fetchedPerson.get().getEndDate());
        assertEquals("These are people notes", fetchedPerson.get().getNotes());
    }

    @Test
    void testSubmitEditBandPageFailsBecauseNameIsRequired() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        Optional<RegionDao> northWest = this.regionService.fetchBySlug("north-west");
        assertTrue(northWest.isPresent());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("surname", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/people/duncan-beckley/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A person must have a surname"));
    }

    @Test
    void testSubmitEditBandPageFailsWhereDatesAreNonsenseIsRequired() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        Optional<RegionDao> northWest = this.regionService.fetchBySlug("north-west");
        assertTrue(northWest.isPresent());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("startDate", "2001-01-01");
        map.add("endDate", "2000-01-01");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/people/duncan-beckley/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("The end date must be after the start date, if both are specified"));
    }

    @Test
    void testSubmitEditBandPageFailsWhereSlugIsNotFound() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Rothwell   Temperance   Band");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + port + "/people/david-roberts/edit", request, String.class));

        // assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

}

