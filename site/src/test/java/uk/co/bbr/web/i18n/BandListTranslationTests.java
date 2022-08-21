package uk.co.bbr.web.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-list-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:band-list-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandListTranslationTests {

    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @Test
    void testGetBandListWorksSuccessfully() {
        String responseTest = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands?lang=test", String.class);
        assertTrue(responseTest.contains("Bands For Test Starting With A"));
        assertTrue(responseTest.contains("This is a list of current or last known names, previous band names can be found by searching."));

        String responseUk = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands?lang=uk", String.class);
        assertTrue(responseUk.contains("Bands starting with A"));
        assertTrue(responseUk.contains("This is a list of current or last known names, previous band names can be found by searching."));
    }

    @Test
    void testGetBandListForSpecificLetterWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/R?lang=test", String.class);
        assertTrue(response.contains("Bands For Test Starting With R"));
        assertTrue(response.contains("This is a list of current or last known names, previous band names can be found by searching."));

        String responseUk = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/R?lang=uk", String.class);
        assertTrue(responseUk.contains("Bands starting with R"));
        assertTrue(responseUk.contains("This is a list of current or last known names, previous band names can be found by searching."));
    }

    @Test
    void testGetAllBandListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/ALL?lang=test", String.class);
        assertTrue(response.contains("Test All Bands"));
        assertTrue(response.contains("This is a list of current or last known names, previous band names can be found by searching."));

        String responseUk = this.restTemplate.getForObject("http://localhost:" + this.port + "/bands/ALL?lang=uk", String.class);
        assertTrue(responseUk.contains("All Bands"));
        assertTrue(responseUk.contains("This is a list of current or last known names, previous band names can be found by searching."));
    }
}
