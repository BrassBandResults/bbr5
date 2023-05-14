package uk.co.bbr.web.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-list-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:band-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandListWebTests implements LoginMixin {

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

        logoutTestUser();
    }

    @Test
    void testGetBandListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Bands - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Bands starting with A</h2>"));
        assertTrue(response.contains("Showing 7 of 11 bands."));

        assertTrue(response.contains("Abercrombie Primary School Community"));
        assertTrue(response.contains("Midlands"));
        assertTrue(response.contains("Accrington Borough"));
        assertTrue(response.contains("North West"));
        assertFalse(response.contains("Black Dyke Band"));
        assertFalse(response.contains("Yorkshire"));
    }

    @Test
    void testGetBandListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/R", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Bands - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>Bands starting with R</h2>"));
        assertTrue(response.contains("Showing 1 of 11 bands."));

        assertTrue(response.contains("Rothwell Temperance"));
        assertTrue(response.contains("Yorkshire"));
        assertFalse(response.contains("Black Dyke Band"));
        assertFalse(response.contains("Abercrombie Primary School Community"));
        assertFalse(response.contains("Midlands"));
    }

    @Test
    void testGetAllBandListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/ALL", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Bands - Brass Band Results</title>"));
        assertTrue(response.contains("<h2>All Bands</h2>"));
        assertTrue(response.contains("Showing 11 of 11 bands."));

        assertTrue(response.contains("Abercrombie Primary School Community"));
        assertTrue(response.contains("Midlands"));
        assertTrue(response.contains("Accrington Borough"));
        assertTrue(response.contains("North West"));
        assertTrue(response.contains("Black Dyke Band"));
        assertTrue(response.contains("Yorkshire"));
        assertTrue(response.contains("102 (Cheshire) Transport Column R.A.S.C. (T.A.)"));
        assertTrue(response.contains("Rothwell Temperance"));
    }
}
