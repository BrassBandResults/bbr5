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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:bands-band-edit-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandEditWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
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

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        this.bandService.create("Grimethorpe", yorkshire);
        this.bandService.create("csrf band", yorkshire);
        this.bandService.create("Grimley", yorkshire);

        logoutTestUser();
    }

    @Test
    void testEditBandPageWorksSuccessfullyForLoggedInUser() {
        // act
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/grimethorpe/edit", String.class);

        // assert
        assertNotNull(response);
        assertTrue(response.contains("Edit Grimethorpe"));
        assertTrue(response.contains("<form action = \"/bands/grimethorpe/edit\""));
        assertTrue(response.contains("selected=\"selected\">Yorkshire</option>"));
        assertTrue(response.contains("selected=\"selected\">Competing</option>"));
        assertTrue(response.contains("value=\"Grimethorpe\""));
    }

    @Test
    void testEditBandPageGetFailsWhereSlugIsNotFound() {
        // act
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/black-dyke/edit", String.class));

        // assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testSubmitEditBandPageSucceedsForLoggedInMember() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
        headers.add("Cookie", SecurityFilter.CSRF_HEADER_NAME + "=" + csrfToken.getToken());

        Optional<RegionDao> northWest = this.regionService.fetchBySlug("north-west");
        assertTrue(northWest.isPresent());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
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
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/bands/grimethorpe/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<BandDao> fetchedBand = this.bandService.fetchBySlug("grimethorpe");
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

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "");
        map.add("_csrf", csrfToken.getToken());
        map.add("_csrf_header", SecurityFilter.CSRF_HEADER_NAME);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/bands/grimethorpe/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A band must have a name"));
    }

    @Test
    void testSubmitEditBandPageFailsWhereDatesAreNonsense() {
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
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/bands/grimethorpe/edit", request, String.class);

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
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + port + "/bands/black-dyke/edit", request, String.class));

        // assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // TODO @Test
    void testSubmitEditBandPageFailsWithNoCsrfToken() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        Optional<RegionDao> northWest = this.regionService.fetchBySlug("north-west");
        assertTrue(northWest.isPresent());

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", "Grimethorpe");
        map.add("region", String.valueOf(northWest.get().getId()));
        map.add("latitude", " 1.23 ");
        map.add("longitude", " 4.56 ");
        map.add("website", " https://BrassBandResults.co.uk ");
        map.add("status", "3");
        map.add("startDate", "1990-01-01");
        map.add("endDate", "2000-02-02");
        map.add("notes", "  These are the notes  ");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/bands/csrf-band/edit", request, String.class);

        // assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        assertNotNull(response.getBody());
    }
}

