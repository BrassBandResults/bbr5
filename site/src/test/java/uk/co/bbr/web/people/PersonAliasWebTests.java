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
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.people.PersonAliasService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.filter.SecurityFilter;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=person-alias-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:person-alias-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonAliasWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PersonService personService;
    @Autowired private PersonAliasService personAliasService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonAliasDao personPreviousName1 = new PersonAliasDao();
        personPreviousName1.setOldName("Dave Roberts");
        personPreviousName1.setHidden(false);
        this.personAliasService.createAlias(davidRoberts, personPreviousName1);
        PersonAliasDao personPreviousName2 = new PersonAliasDao();
        personPreviousName2.setOldName("Davey Roberts");
        personPreviousName2.setHidden(true);
        this.personAliasService.createAlias(davidRoberts, personPreviousName2);

        this.personService.create("Roberts", "John");

        logoutTestUser();
    }

    @Test
    void testListAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit-aliases", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>David Roberts - Person Aliases - Brass Band Results</title>"));
        assertTrue(response.contains(">David Roberts</a>"));
        assertTrue(response.contains("Aliases<"));

        assertTrue(response.contains(">Visible<"));
        assertTrue(response.contains(">Hidden<"));
    }

    @Test
    void testListAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person/edit-aliases", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testHideAliasWorksSuccessfully() {
        Optional<PersonDao> davidRoberts = this.personService.fetchBySlug("david-roberts");

        long visibleAliasId = 0;
        List<PersonAliasDao> previousNamesBefore = this.personAliasService.findAllAliases(davidRoberts.get());
        for (PersonAliasDao previousName : previousNamesBefore) {
            if (previousName.getOldName().equals("Dave Roberts")) {
                assertFalse(previousName.isHidden());
                visibleAliasId = previousName.getId();
                break;
            }
        }

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit-aliases/" + visibleAliasId + "/hide", String.class);
        assertNotNull(response);

        List<PersonAliasDao> previousNamesAfter = this.personAliasService.findAllAliases(davidRoberts.get());
        for (PersonAliasDao previousName : previousNamesAfter) {
            if (previousName.getOldName().equals("Dave Roberts")) {
                assertTrue(previousName.isHidden());
                break;
            }
        }

        this.personAliasService.showAlias(davidRoberts.get(), visibleAliasId);
    }

    @Test
    void testHideAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person/edit-aliases/1/hide", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testHideAliasesWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit-aliases/999/hide", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testShowAliasWorksSuccessfully() {
        Optional<PersonDao> davidRoberts = this.personService.fetchBySlug("david-roberts");

        long hiddenAliasId = 0;
        List<PersonAliasDao> previousNamesBefore = this.personAliasService.findAllAliases(davidRoberts.get());
        for (PersonAliasDao previousName : previousNamesBefore) {
            if (previousName.getOldName().equals("Davey Roberts")) {
                assertTrue(previousName.isHidden());
                hiddenAliasId = previousName.getId();
                break;
            }
        }

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit-aliases/" + hiddenAliasId + "/show", String.class);
        assertNotNull(response);

        List<PersonAliasDao> previousNamesAfter = this.personAliasService.findAllAliases(davidRoberts.get());
        for (PersonAliasDao previousName : previousNamesAfter) {
            if (previousName.getOldName().equals("Davey Roberts")) {
                assertFalse(previousName.isHidden());
                break;
            }
        }

        this.personAliasService.hideAlias(davidRoberts.get(), hiddenAliasId);
    }

    @Test
    void testShowAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person/edit-aliases/1/show", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testShowAliasesWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit-aliases/999/show", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAliasWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        Optional<PersonDao> noAliasPerson = this.personService.fetchBySlug("john-roberts");
        assertTrue(noAliasPerson.isPresent());

        List<PersonAliasDao> fetchedAliases1 = this.personAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(0, fetchedAliases1.size());

        PersonAliasDao previousName = new PersonAliasDao();
        previousName.setOldName("Old Name To Delete");
        PersonAliasDao newAlias = this.personAliasService.createAlias(noAliasPerson.get(), previousName);

        List<PersonAliasDao> fetchedAliases2 = this.personAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(1, fetchedAliases2.size());

        logoutTestUser();

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/john-roberts/edit-aliases/" + newAlias.getId() + "/delete", String.class);
        assertNotNull(response);

        List<PersonAliasDao> fetchedAliases3 = this.personAliasService.findAllAliases(noAliasPerson.get());
        assertEquals(0, fetchedAliases3.size());
    }

    @Test
    void testDeleteAliasWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person/edit-aliases/1/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAliasWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit-aliases/999/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateAliasWorksSuccessfully() {
        // arrange
        Optional<PersonDao> band = this.personService.fetchBySlug("david-roberts");
        assertTrue(band.isPresent());
        List<PersonAliasDao> fetchedAliases1 = this.personAliasService.findAllAliases(band.get());
        assertEquals(2, fetchedAliases1.size());
        long aliasId = fetchedAliases1.get(0).getId();
        assertEquals("Dave Roberts", fetchedAliases1.get(0).getOldName());
        assertEquals("Davey Roberts", fetchedAliases1.get(1).getOldName());

        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("oldName", "Robertsy");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/people/david-roberts/edit-aliases/add", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/people/david-roberts/edit-aliases"));

        List<PersonAliasDao> fetchedAliases2 = this.personAliasService.findAllAliases(band.get());
        assertEquals(3, fetchedAliases2.size());
        assertEquals("Dave Roberts", fetchedAliases2.get(0).getOldName());
        assertEquals("Davey Roberts", fetchedAliases2.get(1).getOldName());
        assertEquals("Robertsy", fetchedAliases2.get(2).getOldName());
        assertFalse(fetchedAliases2.get(2).isHidden());

        logoutTestUserByWeb(this.restTemplate, this.port);
    }

    @Test
    void testCreateAliasWithInvalidBandSlugFailsAsExpected() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("oldName", "New Alias");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + this.port + "/people/not-a-real-person/edit-aliases/add", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

