package uk.co.bbr.web.bands;

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
import uk.co.bbr.services.bands.BandRelationshipService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.dao.BandRelationshipTypeDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
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
@SpringBootTest(properties = { "spring.config.name=band-relationship-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:band-relationship-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandRelationshipsWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private BandRelationshipService bandRelationshipService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandDao warb = this.bandService.create("Wallace Arnold Rothwell Band", yorkshire);

        BandRelationshipDao relationship1 = new BandRelationshipDao();
        relationship1.setLeftBand(warb);
        relationship1.setRightBand(rtb);
        relationship1.setRelationship(this.bandRelationshipService.fetchIsParentOfRelationship());
        this.bandRelationshipService.createRelationship(relationship1);

        BandRelationshipDao relationship2 = new BandRelationshipDao();
        relationship2.setLeftBand(rtb);
        relationship2.setRightBand(warb);
        relationship2.setRelationship(this.bandRelationshipService.fetchIsParentOfRelationship());
        this.bandRelationshipService.createRelationship(relationship2);

        BandDao relationshipToDelete = this.bandService.create("Band With Relationship To Delete", yorkshire);
        BandRelationshipDao toDelete = new BandRelationshipDao();
        toDelete.setLeftBand(relationshipToDelete);
        toDelete.setRightBand(rtb);
        toDelete.setRelationship(this.bandRelationshipService.fetchIsParentOfRelationship());
        this.bandRelationshipService.createRelationship(toDelete);

        this.bandService.create("New Relationship Band");

        logoutTestUser();
    }

    @Test
    void testListRelationshipsWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-relationships", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Rothwell Temperance Band - Band Relationships - Brass Band Results</title>"));
        assertTrue(response.contains(">Rothwell Temperance Band</a>"));
        assertTrue(response.contains("> Relationships<"));

        assertTrue(response.contains(">Wallace Arnold Rothwell Band<"));
    }

    @Test
    void testListRelationshipsWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-a-real-band/edit-relationships", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }


    @Test
    void testDeleteRelationshipWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        Optional<BandDao> bandOptional = this.bandService.fetchBySlug("band-with-relationship-to-delete");
        assertTrue(bandOptional.isPresent());

        List<BandRelationshipDao> fetchedRelationships1 = this.bandRelationshipService.fetchRelationshipsForBand(bandOptional.get());
        assertEquals(1, fetchedRelationships1.size());

        logoutTestUser();

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/band-with-relationship-to-delete/edit-relationships/" + fetchedRelationships1.get(0).getId() + "/delete", String.class);
        assertNotNull(response);

        List<BandRelationshipDao> fetchedRelationships2 = this.bandRelationshipService.fetchRelationshipsForBand(bandOptional.get());
        assertEquals(0, fetchedRelationships2.size());
    }

    @Test
    void testDeleteRelationshipWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-a-real-band/edit-relationships/1/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteRelationshipWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-relationships/999/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateRelationshipWorksSuccessfully() {
        // arrange
        Optional<BandDao> newRelationshipBand = this.bandService.fetchBySlug("new-relationship-band");
        List<BandRelationshipDao> fetchedRelationships1 = this.bandRelationshipService.fetchRelationshipsForBand(newRelationshipBand.get());
        assertEquals(0, fetchedRelationships1.size());

        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        BandRelationshipTypeDao parentRelationship = this.bandRelationshipService.fetchIsParentOfRelationship();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("RightBandSlug", newRelationshipBand.get().getSlug());
        map.add("RightBandName", newRelationshipBand.get().getName());
        map.add("RelationshipTypeId", String.valueOf(parentRelationship.getId()));
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/bands/rothwell-temperance-band/edit-relationships/add", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/bands/rothwell-temperance-band/edit-relationships"));

        List<BandRelationshipDao> fetchedRelationships2 = this.bandRelationshipService.fetchRelationshipsForBand(newRelationshipBand.get());
        assertEquals(1, fetchedRelationships2.size());
        assertEquals("Rothwell Temperance Band", fetchedRelationships2.get(0).getLeftBandName());
        assertEquals("rothwell-temperance-band", fetchedRelationships2.get(0).getLeftBand().getSlug());
        assertEquals("New Relationship Band", fetchedRelationships2.get(0).getRightBandName());
        assertEquals("new-relationship-band", fetchedRelationships2.get(0).getRightBand().getSlug());
        assertEquals("relationship.band.is-parent-of", fetchedRelationships2.get(0).getRelationship().getName());

        logoutTestUserByWeb(this.restTemplate, this.port);
    }

    @Test
    void testCreateRelationshipWithInvalidBandSlugFailsAsExpected() {
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("RightBandSlug", "slug");
        map.add("RightBandName", "name");
        map.add("RelationshipTypeId", "999");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + this.port + "/bands/not-a-real-band/edit-relationships/add", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

