package uk.co.bbr.web.regions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.band.BandService;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=region-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:region-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class RegionWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @Test
    void testGetYorkshireRegionPageWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/regions/yorkshire", String.class);
        assertTrue(response.contains("<h2>Yorkshire</h2>"));
        assertTrue(response.contains("<h3>Map</h3>"));
        assertTrue(response.contains("<h3>Contests</h3>"));
        assertTrue(response.contains("<h3>Bands"));
    }
}
