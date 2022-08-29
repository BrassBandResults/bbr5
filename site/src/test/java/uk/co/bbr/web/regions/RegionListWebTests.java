package uk.co.bbr.web.regions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.band.BandService;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-list-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:band-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegionListWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private RegionService regionService;
    @Autowired private BandService bandService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

     @Test
    void testGetRegionListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/regions", String.class);
        assertTrue(response.contains("<h2>Region List</h2>"));

         assertTrue(response.contains("Angola"));
         assertTrue(response.contains("Yorkshire"));
         assertTrue(response.contains("New Zealand"));

         assertTrue(response.contains("/regions/yorkshire/links"));
         assertTrue(response.contains("/flags/fi.png"));
    }

    @Test
    void testGetYorkshireRegionPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/regions/yorkshire", String.class);
        assertTrue(response.contains("<h2>Yorkshire</h2>"));
        assertTrue(response.contains("<h3>Map</h3>"));
        assertTrue(response.contains("<h3>Contests</h3>"));
        assertTrue(response.contains("<h3>Bands"));
    }
}
