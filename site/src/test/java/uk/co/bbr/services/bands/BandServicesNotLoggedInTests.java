package uk.co.bbr.services.bands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:bands-band-not-logged-in-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class BandServicesNotLoggedInTests {

    @Autowired private BandService bandService;

    @Autowired private RegionService regionService;

    @Test
    void testCreateBandFailsWithoutLogin() {
        BandDao band = new BandDao();
        band.setName("Rothwell Temperance Band");

        AuthenticationCredentialsNotFoundException ex = assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.bandService.create(band));
        assertEquals("An Authentication object was not found in the SecurityContext", ex.getMessage());
    }

    @Test
    void testCreateBandWithNameFailsWithoutLogin() {
        AuthenticationCredentialsNotFoundException ex = assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.bandService.create("Rothwell Temperance Band"));
        assertEquals("An Authentication object was not found in the SecurityContext", ex.getMessage());
    }

    @Test
    void testCreateBandWithNameAndRegionFailsWithoutLogin() {
        RegionDao yorkshireRegion = this.regionService.fetchBySlug("yorkshire").get();
        AuthenticationCredentialsNotFoundException ex = assertThrows(AuthenticationCredentialsNotFoundException.class, () -> this.bandService.create("Black Dyke Band", yorkshireRegion));
        assertEquals("An Authentication object was not found in the SecurityContext", ex.getMessage());
    }
}
