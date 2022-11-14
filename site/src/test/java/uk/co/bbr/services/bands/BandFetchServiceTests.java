package uk.co.bbr.services.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.band.BandService;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.types.RehearsalDay;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-fetch-tests-h2", "spring.datasource.url=jdbc:h2:mem:band-fetch-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandFetchServiceTests implements LoginMixin {
    @Autowired private BandService bandService;
    @Autowired private RegionService regionService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.findBySlug("yorkshire");
        this.bandService.create("Black Dyke Band", yorkshire);

        logoutTestUser();
    }

    @Test
    void testFetchBandBySlugWorksCorrectly() {
        // act
        BandDao band = this.bandService.findBandBySlug("black-dyke-band");

        // assert
        assertNotNull(band.getId());
        assertNotNull(band.getCreated());
        assertEquals(1, band.getCreatedBy());
        assertNotNull(band.getUpdated());
        assertEquals(1, band.getUpdatedBy());
        assertEquals("Black Dyke Band", band.getName());
        assertEquals("black-dyke-band", band.getSlug());
        assertEquals("Unknown", band.getRegion().getName());
    }
}


