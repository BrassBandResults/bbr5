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
import uk.co.bbr.services.people.PersonRelationshipService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.people.dao.PersonRelationshipTypeDao;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=person-relationship-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:person-relationship-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonRelationshipsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private PersonService personService;
    @Autowired private PersonRelationshipService personRelationshipService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupPersons() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao gordonRoberts = this.personService.create("Roberts", "Gordon");

        PersonRelationshipDao relationship1 = new PersonRelationshipDao();
        relationship1.setLeftPerson(gordonRoberts);
        relationship1.setRightPerson(davidRoberts);
        relationship1.setRelationship(this.personRelationshipService.fetchTypeByName("relationship.person.is-father-of"));
        this.personRelationshipService.createRelationship(relationship1);

        PersonRelationshipDao relationship2 = new PersonRelationshipDao();
        relationship2.setLeftPerson(davidRoberts);
        relationship2.setRightPerson(johnRoberts);
        relationship2.setRelationship(this.personRelationshipService.fetchTypeByName("relationship.person.is-brother-of"));
        this.personRelationshipService.createRelationship(relationship2);

        this.personService.create("Person", "New Relationship");
        this.personService.create("Person", "New Relationship 2");

        PersonDao deletePerson = this.personService.create("Delete", "Person with relationship to");

        PersonRelationshipDao relationshipToDelete = new PersonRelationshipDao();
        relationshipToDelete.setLeftPerson(deletePerson);
        relationshipToDelete.setRightPerson(davidRoberts);
        relationshipToDelete.setRelationship(this.personRelationshipService.fetchTypeByName("relationship.person.is-father-of"));
        this.personRelationshipService.createRelationship(relationshipToDelete);


        logoutTestUser();
    }

    @Test
    void testListRelationshipsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit-relationships", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>David Roberts - Person Relationships - Brass Person Results</title>"));
        assertTrue(response.contains(">David Roberts</a>"));
        assertTrue(response.contains("> Relationships<"));

        assertTrue(response.contains(">Gordon Roberts<"));
    }

    @Test
    void testListRelationshipsWithInvalidPersonSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person/edit-relationships", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }


    @Test
    void testDeleteRelationshipWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        Optional<PersonDao> personOptional = this.personService.fetchBySlug("person-with-relationship-to-delete");
        assertTrue(personOptional.isPresent());

        List<PersonRelationshipDao> fetchedRelationships1 = this.personRelationshipService.fetchRelationshipsForPerson(personOptional.get());
        assertEquals(1, fetchedRelationships1.size());

        logoutTestUser();

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/person-with-relationship-to-delete/edit-relationships/" + fetchedRelationships1.get(0).getId() + "/delete", String.class);
        assertNotNull(response);

        List<PersonRelationshipDao> fetchedRelationships2 = this.personRelationshipService.fetchRelationshipsForPerson(personOptional.get());
        assertEquals(0, fetchedRelationships2.size());
    }

    @Test
    void testDeleteRelationshipWithInvalidPersonSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/not-a-real-person/edit-relationships/1/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteRelationshipWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/people/david-roberts/edit-relationships/999/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateRelationshipWorksSuccessfully() {
        // arrange
        Optional<PersonDao> newRelationshipPerson = this.personService.fetchBySlug("new-relationship-person");
        List<PersonRelationshipDao> fetchedRelationships1 = this.personRelationshipService.fetchRelationshipsForPerson(newRelationshipPerson.get());
        assertEquals(0, fetchedRelationships1.size());

        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        PersonRelationshipTypeDao parentRelationship = this.personRelationshipService.fetchTypeByName("relationship.person.is-father-of");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("RightPersonSlug", newRelationshipPerson.get().getSlug());
        map.add("RelationshipTypeId", String.valueOf(parentRelationship.getId()));
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/people/david-roberts/edit-relationships/add", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/people/david-roberts/edit-relationships"));

        List<PersonRelationshipDao> fetchedRelationships2 = this.personRelationshipService.fetchRelationshipsForPerson(newRelationshipPerson.get());
        assertEquals(1, fetchedRelationships2.size());
        assertEquals("david-roberts", fetchedRelationships2.get(0).getLeftPerson().getSlug());
        assertEquals("new-relationship-person", fetchedRelationships2.get(0).getRightPerson().getSlug());
        assertEquals("relationship.person.is-father-of", fetchedRelationships2.get(0).getRelationship().getName());

        logoutTestUserByWeb(this.restTemplate, this.port);
    }

    @Test
    void testCreateRelationshipWithInvalidPersonSlugFailsAsExpected() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("RightPersonSlug", "slug");
        map.add("RelationshipTypeId", "1");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + this.port + "/people/not-a-real-person/edit-relationships/add", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateRelationshipWithInvalidRightPersonSlugFailsAsExpected() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("RightPersonSlug", "slug");
        map.add("RelationshipTypeId", "1");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + this.port + "/people/david-roberts/edit-relationships/add", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateRelationshipWithInvalidRelationshipIdFailsAsExpected() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("RightPersonSlug", "gordon-roberts");
        map.add("RelationshipTypeId", "999");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + this.port + "/people/david-roberts/edit-relationships/add", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

