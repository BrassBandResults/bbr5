package uk.co.bbr.services.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.RehearsalDay;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=rehearsal-tests-h2", "spring.datasource.url=jdbc:h2:mem:rehearsal-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateRehearsalNightServiceTests implements LoginMixin {
    @Autowired private BandService bandService;
    @Autowired private RegionService regionService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();
        this.bandService.create("Black Dyke Band", yorkshire);

        logoutTestUser();
    }

    @Test
    void testAddBandRehearsalNightWorksCorrectly() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.fetchBySlug("black-dyke-band").get();

        // act
        this.bandService.createRehearsalNight(band, RehearsalDay.FRIDAY);
        this.bandService.createRehearsalNight(band, RehearsalDay.MONDAY);

        // assert
        List<RehearsalDay> bandRehearsalDays = this.bandService.findRehearsalNights(band);
        assertEquals(2, bandRehearsalDays.size());
        assertEquals(RehearsalDay.MONDAY, bandRehearsalDays.get(0));
        assertEquals(RehearsalDay.FRIDAY, bandRehearsalDays.get(1));

        logoutTestUser();
    }
}


