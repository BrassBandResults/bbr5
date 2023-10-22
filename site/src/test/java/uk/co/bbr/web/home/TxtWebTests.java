package uk.co.bbr.web.home;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.web.LoginMixin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:home-txt-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TxtWebTests implements LoginMixin {

    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @Test
    void testGetAdsTxtWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/ads.txt", String.class);
        assertNotNull(response);
        assertTrue(response.contains("google.com, pub"));
    }
}
