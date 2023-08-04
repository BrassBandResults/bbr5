package uk.co.bbr.web.bands;

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
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandRehearsalsService;
import uk.co.bbr.services.bands.BandRelationshipService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.events.ResultService;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
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
        "spring.datasource.url=jdbc:h2:mem:band-delete-band-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteBandWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private BandService bandService;
    @Autowired private BandAliasService bandAliasService;
    @Autowired private UserService userService;
    @Autowired private BandRehearsalsService bandRehearsalsService;
    @Autowired private BandRelationshipService bandRelationshipService;
    @Autowired private PersonService personService;
    @Autowired private ResultService resultService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testDeleteBandWithNoResultsSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.bandService.create("Band One");

        Optional<BandDao> beforeDelete = this.bandService.fetchBySlug("band-one");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/bands/band-one/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Bands starting with A<"));

        Optional<BandDao> afterDelete = this.bandService.fetchBySlug("band-one");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteBandWithAliasesSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.create("Band Two");
        this.bandAliasService.createAlias(band, "Band Two A");

        Optional<BandDao> beforeDelete = this.bandService.fetchBySlug("band-two");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/bands/band-two/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Bands starting with A<"));

        Optional<BandDao> afterDelete = this.bandService.fetchBySlug("band-two");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteBandWithRehearsalDaysSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.create("Band Two A");
        this.bandRehearsalsService.createRehearsalDay(band, RehearsalDay.MONDAY);

        Optional<BandDao> beforeDelete = this.bandService.fetchBySlug("band-two-a");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/bands/band-two-a/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Bands starting with A<"));

        Optional<BandDao> afterDelete = this.bandService.fetchBySlug("band-two-b");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteBandWithRelationshipsSucceeds() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.create("Band Two B");
        BandDao bandTwo1 = this.bandService.create("Other Two B1");
        BandDao bandTwo2 = this.bandService.create("Other Two B2");
        BandRelationshipTypeDao parent = this.bandRelationshipService.fetchIsParentOfRelationship();
        this.bandRelationshipService.createRelationship(band, bandTwo1, parent);
        this.bandRelationshipService.createRelationship(bandTwo2, band, parent);

        Optional<BandDao> beforeDelete = this.bandService.fetchBySlug("band-two-b");
        assertTrue(beforeDelete.isPresent());

        ResponseEntity<String> response = this.restTemplate.getForEntity("http://localhost:" + this.port + "/bands/band-two-b/delete", String.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(">Bands starting with A<"));

        Optional<BandDao> afterDelete = this.bandService.fetchBySlug("band-two-b");
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void testDeleteBandWithResultsFailsAsExpected() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.create("Band Three");
        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestEventDao yorkshire2010 = this.contestEventService.create(yorkshireArea, LocalDate.of(2010, 3, 1));
        PersonDao conductor = this.personService.create("Conductor", "New");
        this.resultService.addResult(yorkshire2010, "1", band, conductor);

        Optional<BandDao> beforeDelete = this.bandService.fetchBySlug("band-three");
        assertTrue(beforeDelete.isPresent());

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/band-three/delete", String.class);
        assertNotNull(response);
        assertTrue(response.contains("This band has results and cannot be deleted."));

        Optional<BandDao> afterDelete = this.bandService.fetchBySlug("band-three");
        assertFalse(afterDelete.isEmpty());
    }
}
