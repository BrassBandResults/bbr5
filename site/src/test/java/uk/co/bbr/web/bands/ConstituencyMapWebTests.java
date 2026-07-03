package uk.co.bbr.web.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:bands-constituency-map-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConstituencyMapWebTests implements LoginMixin {

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
        BandDao yorkshireBand = this.bandService.create("Rothwell Temperance Band", yorkshire);
        yorkshireBand.setLatitude("53.0");
        yorkshireBand.setLongitude("-1.0");
        this.bandService.update(yorkshireBand);

        RegionDao ni = this.regionService.fetchBySlug("northern-ireland").get();
        BandDao niBand = this.bandService.create("NI Band", ni);
        niBand.setLatitude("54.0");
        niBand.setLongitude("-6.0");
        this.bandService.update(niBand);

        RegionDao iom = this.regionService.fetchBySlug("isle-of-man").get();
        BandDao iomBand = this.bandService.create("IOM Band", iom);
        iomBand.setLatitude("54.2");
        iomBand.setLongitude("-4.5");
        this.bandService.update(iomBand);

        logoutTestUser();
    }

    @Test
    void testGetConstituencyMapWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/MAP/consituencies", String.class);
        assertNotNull(response);
        assertTrue(response.contains("<title>Band Map - Brass Band Results</title>"));
        assertTrue(response.contains("Constituencies"));
        assertTrue(response.contains("/bands/MAP/consituencies/bands.json"));
    }

    @Test
    void testGetConstituencyBandsJsonWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/MAP/consituencies/bands.json", String.class);
        assertNotNull(response);
        assertTrue(response.contains("FeatureCollection"));
        assertTrue(response.contains("Rothwell Temperance Band"));
        assertTrue(response.contains("NI Band"));
        assertTrue(response.contains("IOM Band"));
    }
}
