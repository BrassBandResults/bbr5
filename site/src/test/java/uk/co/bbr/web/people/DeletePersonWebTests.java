package uk.co.bbr.web.people;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.PersonResultService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.people.PersonAliasService;
import uk.co.bbr.services.people.PersonRelationshipService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.people.dao.PersonRelationshipTypeDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.UserService;
import uk.co.bbr.services.security.dao.SiteUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:people-delete-person-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeletePersonWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private UserService userService;
    @Autowired private BandService bandService;
    @Autowired private PieceService pieceService;
    @Autowired private ResultService resultService;
    @Autowired private PersonService personService;
    @Autowired private PersonAliasService personAliasService;
    @Autowired private PersonRelationshipService personRelationshipService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testDeletePersonWithNoLinksSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.personService.create("One", "Person");

        Optional<PersonDao> beforeDelete = this.personService.fetchBySlug("person-one");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/people/person-one/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">People with surnames starting with A<"));

        Optional<PersonDao> afterDelete = this.personService.fetchBySlug("person-one");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeletePersonWithAliasesSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = this.personService.create("Two", "Person");
        PersonAliasDao newAlias = new PersonAliasDao();
        newAlias.setOldName("Person Alias");
        this.personAliasService.createAlias(person, newAlias);

        Optional<PersonDao> beforeDelete = this.personService.fetchBySlug("person-two");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/people/person-two/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">People with surnames starting with A<"));

        Optional<PersonDao> afterDelete = this.personService.fetchBySlug("person-two");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeletePersonWithRelationshipsSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = this.personService.create("Three", "Person");
        PersonDao personTwo = this.personService.create("Three A", "Relationship");
        PersonDao personThree = this.personService.create("Three B", "Relationship");
        PersonRelationshipTypeDao parent = this.personRelationshipService.fetchTypeByName("relationship.person.is-father-of");
        this.personRelationshipService.createRelationship(person, personTwo, parent);
        this.personRelationshipService.createRelationship(personThree, person, parent);

        Optional<PersonDao> beforeDelete = this.personService.fetchBySlug("person-three");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/people/person-three/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">People with surnames starting with A<"));

        Optional<PersonDao> afterDelete = this.personService.fetchBySlug("person-three");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeletePersonWithConductingRecordFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = this.personService.create("Four", "Person");
        ContestDao contest = this.contestService.create("Yorkshire Area Four");
        ContestEventDao contestEvent = this.contestEventService.create(contest, LocalDate.of(2011, 3, 1));
        BandDao band = this.bandService.create("Rothwell Temperance");
        this.resultService.addResult(contestEvent, "1", band, person);

        Optional<PersonDao> beforeDelete = this.personService.fetchBySlug("person-four");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/person-four/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This person is used and cannot be deleted."));

        Optional<PersonDao> afterDelete = this.personService.fetchBySlug("person-four");
        assertFalse(afterDelete.isEmpty());
    }

    @Test
    void testDeletePersonWithAdjudicationsFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = this.personService.create("Five", "Person");
        ContestDao contest = this.contestService.create("Yorkshire Area Five");
        ContestEventDao contestEvent = this.contestEventService.create(contest, LocalDate.of(2011, 3, 1));
        this.contestEventService.addAdjudicator(contestEvent, person);

        Optional<PersonDao> beforeDelete = this.personService.fetchBySlug("person-five");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/person-five/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This person is used and cannot be deleted."));

        Optional<PersonDao> afterDelete = this.personService.fetchBySlug("person-five");
        assertFalse(afterDelete.isEmpty());
    }

    @Test
    void testDeletePersonWithCompositionsFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = this.personService.create("Six", "Person");
        PieceDao piece = this.pieceService.create("Piece Six");
        piece.setComposer(person);
        this.pieceService.update(piece);

        Optional<PersonDao> beforeDelete = this.personService.fetchBySlug("person-six");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/person-six/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This person is used and cannot be deleted."));

        Optional<PersonDao> afterDelete = this.personService.fetchBySlug("person-six");
        assertFalse(afterDelete.isEmpty());
    }

    @Test
    void testDeletePersonWithArrangementsFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        PersonDao person = this.personService.create("Seven", "Person");
        PieceDao piece = this.pieceService.create("Piece Seven");
        piece.setArranger(person);
        this.pieceService.update(piece);

        Optional<PersonDao> beforeDelete = this.personService.fetchBySlug("person-seven");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/people/person-seven/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This person is used and cannot be deleted."));

        Optional<PersonDao> afterDelete = this.personService.fetchBySlug("person-seven");
        assertFalse(afterDelete.isEmpty());
    }
}
