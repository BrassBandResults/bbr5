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
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
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
@SpringBootTest(properties = { "spring.config.name=band-create-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:band-create-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandCreateWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_MEMBER.getUsername(), TestUser.TEST_MEMBER.getPassword(), TestUser.TEST_MEMBER.getEmail());
        loginTestUserByWeb(TestUser.TEST_MEMBER, this.restTemplate, this.csrfTokenRepository, this.port);
    }

    @Test
    void testCreateBandPageWorksSuccessfullyForLoggedInUser() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/create/band", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Create Band"));
        assertTrue(response.contains("<form action = \"/create/band\""));
        assertTrue(response.contains("selected=\"selected\">Unknown</option>"));
    }

    @Test
    void testSubmitCreateBandPageSucceedsForLoggedInMember() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        Optional<RegionDao> northWest = this.regionService.fetchBySlug("north-west");
        assertTrue(northWest.isPresent());

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("name", "Rothwell   Temperance   Band");
        map.add("region", String.valueOf(northWest.get().getId()));
        map.add("latitude", " 1.23 ");
        map.add("longitude", " 4.56 ");
        map.add("website", " https://BrassBandResults.co.uk ");
        map.add("status", "3");
        map.add("startDate", "1990-01-01");
        map.add("endDate", "2000-02-02");
        map.add("notes", "  These are the notes  ");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/band", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/bands"));

        Optional<BandDao> fetchedBand = this.bandService.fetchBySlug("rothwell-temperance-band");
        assertTrue(fetchedBand.isPresent());
        assertEquals("Rothwell Temperance Band", fetchedBand.get().getName());
        assertEquals("North West", fetchedBand.get().getRegion().getName());
        assertEquals("1.23", fetchedBand.get().getLatitude());
        assertEquals("4.56", fetchedBand.get().getLongitude());
        assertEquals("https://BrassBandResults.co.uk", fetchedBand.get().getWebsite());
        assertEquals(3, fetchedBand.get().getStatus().getCode());
        assertEquals(LocalDate.of(1990, 1, 1), fetchedBand.get().getStartDate());
        assertEquals(LocalDate.of(2000, 2, 2), fetchedBand.get().getEndDate());
        assertEquals("These are the notes", fetchedBand.get().getNotes());
    }

    @Test
    void testSubmitEditBandPageFailsBecauseNameIsRequired() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        Optional<RegionDao> northWest = this.regionService.fetchBySlug("north-west");
        assertTrue(northWest.isPresent());

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("name", "");
        map.add("region", String.valueOf(northWest.get().getId()));
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/band", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A band must have a name"));
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

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("name", "New Band");
        map.add("region", String.valueOf(northWest.get().getId()));
        map.add("startDate", "2001-01-01");
        map.add("endDate", "2000-01-01");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/create/band", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("The end date must be after the start date, if both are specified"));
    }
}

