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
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandAliasDao;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-alias-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:band-alias-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandAliasWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private BandAliasService bandAliasService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        // band with two aliases
        BandDao rtb = this.bandService.create("Rothwell Temperance Band", yorkshire);
        BandAliasDao bandPreviousName1 = new BandAliasDao();
        bandPreviousName1.setOldName("Visible");
        bandPreviousName1.setHidden(false);
        this.bandAliasService.createAlias(rtb, bandPreviousName1);

        BandAliasDao bandPreviousName2 = new BandAliasDao();
        bandPreviousName2.setOldName("Hidden");
        bandPreviousName2.setHidden(true);
        this.bandAliasService.createAlias(rtb, bandPreviousName2);

        // band with no aliases
        this.bandService.create("Black Dyke", yorkshire);

        logoutTestUser();
    }

    @Test
    void testListAliasesWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-aliases", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Rothwell Temperance Band - Band Aliases - Brass Band Results</title>"));
        assertTrue(response.contains(">Rothwell Temperance Band</a>"));
        assertTrue(response.contains("> Aliases<"));

        assertTrue(response.contains(">Visible<"));
        assertTrue(response.contains(">Hidden<"));
    }

    @Test
    void testListAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-a-real-band/edit-aliases", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testHideAliasWorksSuccessfully() {
        Optional<BandDao> rtb = this.bandService.fetchBySlug("rothwell-temperance-band");

        long visibleAliasId = 0;
        List<BandAliasDao> previousNamesBefore = this.bandAliasService.findAllAliases(rtb.get());
        for (BandAliasDao previousName : previousNamesBefore) {
            if (previousName.getOldName().equals("Visible")) {
                assertFalse(previousName.isHidden());
                visibleAliasId = previousName.getId();
                break;
            }
        }

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-aliases/" + visibleAliasId + "/hide", String.class);
        assertNotNull(response);

        List<BandAliasDao> previousNamesAfter = this.bandAliasService.findAllAliases(rtb.get());
        for (BandAliasDao previousName : previousNamesAfter) {
            if (previousName.getOldName().equals("Visible")) {
                assertTrue(previousName.isHidden());
                break;
            }
        }

        this.bandAliasService.showAlias(rtb.get(), visibleAliasId);
    }

    @Test
    void testHideAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-a-real-band/edit-aliases/1/hide", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testHideAliasesWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-aliases/999/hide", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testShowAliasWorksSuccessfully() {
        Optional<BandDao> rtb = this.bandService.fetchBySlug("rothwell-temperance-band");

        long hiddenAliasId = 0;
        List<BandAliasDao> previousNamesBefore = this.bandAliasService.findAllAliases(rtb.get());
        for (BandAliasDao previousName : previousNamesBefore) {
            if (previousName.getOldName().equals("Hidden")) {
                assertTrue(previousName.isHidden());
                hiddenAliasId = previousName.getId();
                break;
            }
        }

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-aliases/" + hiddenAliasId + "/show", String.class);
        assertNotNull(response);

        List<BandAliasDao> previousNamesAfter = this.bandAliasService.findAllAliases(rtb.get());
        for (BandAliasDao previousName : previousNamesAfter) {
            if (previousName.getOldName().equals("Hidden")) {
                assertFalse(previousName.isHidden());
                break;
            }
        }

        this.bandAliasService.hideAlias(rtb.get(), hiddenAliasId);
    }

    @Test
    void testShowAliasesWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-a-real-band/edit-aliases/1/show", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testShowAliasesWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-aliases/999/show", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAliasWorksSuccessfully() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        Optional<BandDao> noAliasBand = this.bandService.fetchBySlug("black-dyke");
        assertTrue(noAliasBand.isPresent());

        List<BandAliasDao> fetchedAliases1 = this.bandAliasService.findAllAliases(noAliasBand.get());
        assertEquals(0, fetchedAliases1.size());

        BandAliasDao previousName = new BandAliasDao();
        previousName.setOldName("Old Name To Delete");
        BandAliasDao newAlias = this.bandAliasService.createAlias(noAliasBand.get(), previousName);

        List<BandAliasDao> fetchedAliases2 = this.bandAliasService.findAllAliases(noAliasBand.get());
        assertEquals(1, fetchedAliases2.size());

        logoutTestUser();

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/black-dyke/edit-aliases/" + newAlias.getId() + "/delete", String.class);
        assertNotNull(response);

        List<BandAliasDao> fetchedAliases3 = this.bandAliasService.findAllAliases(noAliasBand.get());
        assertEquals(0, fetchedAliases3.size());
    }

    @Test
    void testDeleteAliasWithInvalidBandSlugFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/not-a-real-band/edit-aliases/1/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAliasWithInvalidAliasIdFailsAsExpected() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/rothwell-temperance-band/edit-aliases/999/delete", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateAliasWorksSuccessfully() {
        // arrange
        Optional<BandDao> band = this.bandService.fetchBySlug("rothwell-temperance-band");
        assertTrue(band.isPresent());
        List<BandAliasDao> fetchedAliases1 = this.bandAliasService.findAllAliases(band.get());
        assertEquals(2, fetchedAliases1.size());
        long aliasId = fetchedAliases1.get(0).getId();
        assertEquals("Hidden", fetchedAliases1.get(0).getOldName());
        assertEquals("Visible", fetchedAliases1.get(1).getOldName());

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

        // act
        ResponseEntity<String> response = this.restTemplate.postForEntity("http://localhost:" + port + "/bands/rothwell-temperance-band/edit-aliases/add", request, String.class);

        // assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());

        assertTrue(Objects.requireNonNull(response.getHeaders().get("Location")).get(0).endsWith("/bands/rothwell-temperance-band/edit-aliases"));

        List<BandAliasDao> fetchedAliases2 = this.bandAliasService.findAllAliases(band.get());
        assertEquals(3, fetchedAliases2.size());
        assertEquals("Hidden", fetchedAliases2.get(0).getOldName());
        assertEquals("New Alias", fetchedAliases2.get(1).getOldName());
        assertFalse(fetchedAliases2.get(1).isHidden());
        assertEquals("Visible", fetchedAliases2.get(2).getOldName());

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

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.postForEntity("http://localhost:" + this.port + "/bands/not-a-real-band/edit-aliases/add", request, String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }


}

