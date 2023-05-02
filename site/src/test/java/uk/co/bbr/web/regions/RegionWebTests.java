package uk.co.bbr.web.regions;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=region-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:region-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegionWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao midlands = this.regionService.fetchBySlug("midlands").get();
        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();
        RegionDao northWest = this.regionService.fetchBySlug("north-west").get();
        RegionDao wales = this.regionService.fetchBySlug("wales").get();
        RegionDao norway = this.regionService.fetchBySlug("norway").get();
        RegionDao denmark = this.regionService.fetchBySlug("denmark").get();

        this.bandService.create("Abercrombie Primary School Community", midlands);
        this.bandService.create("Black Dyke Band", yorkshire);
        this.bandService.create("Accrington Borough", northWest);
        this.bandService.create("Rothwell Temperance", yorkshire);
        this.bandService.create("48th Div. R.E. T.A", midlands);
        this.bandService.create("Aalesunds Ungdomsmusikkorps", norway);
        this.bandService.create("Abb Kettleby", midlands);
        this.bandService.create("Acceler8", northWest);
        this.bandService.create("Aberllefenni", wales);
        this.bandService.create("Aalborg Brass Band", denmark);
        this.bandService.create("102 (Cheshire) Transport Column R.A.S.C. (T.A.)", northWest);

        BandDao extinct = this.bandService.create("Extinct Yorkshire", yorkshire);
        extinct.setStatus(BandStatus.EXTINCT);
        extinct.setLatitude("0.00");
        extinct.setLongitude("0.00");
        this.bandService.update(extinct);

        logoutTestUser();
    }

    @Test
    void testGetYorkshireRegionPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/regions/yorkshire", String.class);
        assertTrue(response.contains("<title>Yorkshire - Region - Brass Band Results</title>"));

        assertTrue(response.contains("<h2>Yorkshire</h2>"));
        assertTrue(response.contains("<h3>Contests</h3>"));
        assertTrue(response.contains("<h3>Bands"));

        assertTrue(response.contains("Rothwell Temperance"));
        assertFalse(response.contains("Accrington Borough"));
    }

    @Test
    void testGetYorkshireRegionLinksPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/regions/yorkshire/links", String.class);
        assertTrue(response.contains("<title>Yorkshire - Region Links - Brass Band Results</title>"));

        assertTrue(response.contains("<h2>Yorkshire</h2>"));

        assertTrue(response.contains("Rothwell Temperance"));
        assertFalse(response.contains("Accrington Borough"));
    }

    @Test
    void testGetYorkshireRegionLinksPageWithInvalidSlugFails() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/regions/not-a-region-slug/links", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetYorkshireExtinctBandsGeoJsonWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/regions/yorkshire/status.extinct/bands.json", String.class);
        DocumentContext parsedJson = JsonPath.parse(response);
        assertEquals("FeatureCollection", parsedJson.read("$['type']"));
    }

    @Test
    void testGetYorkshireExtinctBandsGeoJsonWithInvalidSlugFails() {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + "/regions/not-a-region-slug/status.extinct/bands.json", String.class));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
