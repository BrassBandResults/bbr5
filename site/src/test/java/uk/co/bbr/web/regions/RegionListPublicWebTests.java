package uk.co.bbr.web.regions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.web.LoginMixin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=region-list-public-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:region-list-public-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegionListPublicWebTests implements LoginMixin {

    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

     @Test
    void testGetRegionListWorksSuccessfully() {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + "/regions", String.class);
         assertNotNull(response);
         assertTrue(response.contains("<title>Regions - Brass Band Results</title>"));
         assertTrue(response.contains("<h2>Regions</h2>"));

         assertTrue(response.contains("Angola"));
         assertTrue(response.contains("Yorkshire"));
         assertTrue(response.contains("New Zealand"));

         assertTrue(response.contains("/regions/yorkshire/links"));
         assertTrue(response.contains("/flags/fi.png"));

         // band count is aggregate only for non-pro users
         assertTrue(response.contains("class=\"band-count\""));
         assertTrue(response.contains("<th>Bands</th>"));
         assertFalse(response.contains("class=\"active-band-count\""));
         assertFalse(response.contains("class=\"extinct-band-count\""));
         assertFalse(response.contains("<th>Active Bands</th>"));
         assertFalse(response.contains("<th>Extinct Bands</th>"));
    }
}
