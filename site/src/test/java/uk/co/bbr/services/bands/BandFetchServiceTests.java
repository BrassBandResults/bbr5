package uk.co.bbr.services.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-fetch-tests-h2", "spring.datasource.url=jdbc:h2:mem:band-fetch-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandFetchServiceTests implements LoginMixin {
    @Autowired private BandService bandService;
    @Autowired private SectionService sectionService;
    @Autowired private RegionService regionService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.findBySlug("yorkshire");

        BandDao blackDyke = this.bandService.create("Black Dyke Band", yorkshire);
        blackDyke.setSection(this.sectionService.fetchBySlug("championship"));
        this.bandService.update(blackDyke);

        BandDao rothwell = this.bandService.create("Rothwell Temperance", yorkshire);
        rothwell.setStartDate(LocalDate.of(1985, 6, 6));
        this.bandService.update(rothwell);

        BandDao wallaceArnold = this.bandService.create("Wallace Arnold (Rothwell) Band", yorkshire);
        wallaceArnold.setStartDate(LocalDate.of(1881, 3, 5));
        wallaceArnold.setEndDate(LocalDate.of(2000, 12, 8));
        wallaceArnold.setStatus(BandStatus.SCRATCH);
        wallaceArnold.setOldId("123");
        this.bandService.update(wallaceArnold);

        BandDao rothwellOld = this.bandService.create("Rothwell Old", yorkshire);
        rothwellOld.setEndDate(LocalDate.of(1980, 4, 2));
        rothwellOld.setStatus(BandStatus.EXTINCT);
        this.bandService.update(rothwellOld);

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
        assertTrue(band.getUpdatedBy() > 0);
        assertEquals("Black Dyke Band", band.getName());
        assertEquals("black-dyke-band", band.getSlug());
        assertEquals("Yorkshire", band.getRegion().getName());
        assertEquals("Championship", band.getSection().getName());
        assertEquals("section.championship", band.getSectionType());
    }

    @Test
    void testFullDateRangeWorksCorrectly() {
        // act
        BandDao band = this.bandService.findBandBySlug("wallace-arnold-rothwell-band");

        // assert
        assertEquals("1881-2000", band.getDateRange());
        assertEquals("status.scratch", band.getSectionType());
        assertEquals("123", band.getOldId());
    }

    @Test
    void testEndDateRangeWorksCorrectly() {
        // act
        BandDao band = this.bandService.findBandBySlug("rothwell-old");

        // assert
        assertEquals("-1980", band.getDateRange());
        assertEquals("status.extinct", band.getSectionType());
    }

    @Test
    void testStartDateRangeWorksCorrectly() {
        // act
        BandDao band = this.bandService.findBandBySlug("rothwell-temperance");

        // assert
        assertEquals("1985-", band.getDateRange());
        assertEquals("status.competing", band.getSectionType());
    }
}


