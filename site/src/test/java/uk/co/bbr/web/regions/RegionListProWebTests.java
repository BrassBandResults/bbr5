package uk.co.bbr.web.regions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=region-list-pro-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:region-list-pro-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegionListProWebTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private CsrfTokenRepository csrfTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupUser() {
        this.securityService.createUser(TestUser.TEST_PRO.getUsername(), TestUser.TEST_PRO.getPassword(), TestUser.TEST_PRO.getEmail());
        this.securityService.makeUserPro(TestUser.TEST_PRO.getUsername());
    }

    @Test
    void testGetReturnListAsProUserWorksSuccessfully() {
        loginTestUserByWeb(TestUser.TEST_PRO, this.restTemplate, this.csrfTokenRepository, this.port);

        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/regions", String.class);
        assertTrue(response.contains("<h2>Region List</h2>"));

        assertTrue(response.contains("Angola"));
        assertTrue(response.contains("Yorkshire"));
        assertTrue(response.contains("New Zealand"));

        assertTrue(response.contains("/regions/yorkshire/links"));
        assertTrue(response.contains("/flags/fi.png"));

        // band count is broken down for pro users
        assertFalse(response.contains("class=\"band-count\""));
        assertFalse(response.contains("<th>Bands</th>"));
        assertTrue(response.contains("class=\"active-band-count\""));
        assertTrue(response.contains("class=\"extinct-band-count\""));
        assertTrue(response.contains("<th>Active Bands</th>"));
        assertTrue(response.contains("<th>Extinct Bands</th>"));
    }
}
