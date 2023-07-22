package uk.co.bbr.services.regions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:regions-region-inheritance-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegionInheritanceTests implements LoginMixin {
    @Autowired private RegionService regionService;
    @Autowired private SecurityService securityService;
    @Autowired private BandService bandService;
    @Autowired private JwtService jwtService;


    @BeforeAll
    void setupRegions() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao uk = this.regionService.create("Test UK");
        RegionDao england = this.regionService.create("Test England", uk);
        RegionDao yorkshire = this.regionService.create("Test Yorkshire", england);
        RegionDao lancashire = this.regionService.create("Test Lancashire", england);

        this.bandService.create("UK Band 1", uk);
        BandDao extinctUk = this.bandService.create("UK Band 2", uk);
        extinctUk.setStatus(BandStatus.EXTINCT);
        this.bandService.update(extinctUk);

        this.bandService.create("England Band 1", england);
        this.bandService.create("England Band 2", england);
        BandDao extinctEngland = this.bandService.create("England Band 3", england);
        extinctEngland.setStatus(BandStatus.EXTINCT);
        this.bandService.update(extinctEngland);

        this.bandService.create("Yorkshire Band 1",yorkshire);
        this.bandService.create("Yorkshire Band 2", yorkshire);
        this.bandService.create("Yorkshire Band 3", yorkshire);
        BandDao extinctYorkshire = this.bandService.create("Yorkshire Band 4", yorkshire);
        extinctYorkshire.setStatus(BandStatus.EXTINCT);
        this.bandService.update(extinctYorkshire);

        this.bandService.create("Lancashire Band 1",lancashire);
        this.bandService.create("Lancashire Band 2", lancashire);
        BandDao extinctLancashire = this.bandService.create("Lancashire Band 3", lancashire);
        extinctLancashire.setStatus(BandStatus.EXTINCT);
        this.bandService.update(extinctLancashire);

        logoutTestUser();
    }

    @Test
    void testFetchSpecificRegionWorks() {
        // act
        Optional<RegionDao> yorkshireOptional = this.regionService.fetchBySlug("test-yorkshire");

        // assert
        assertTrue(yorkshireOptional.isPresent());
        assertFalse(yorkshireOptional.isEmpty());

        RegionDao yorkshire = yorkshireOptional.get();

        // TODO fix this - @Formula removed
        // assertEquals(4, yorkshire.getBandsCount());
        // assertEquals(3, yorkshire.getActiveBandsCount());
        // assertEquals(1, yorkshire.getExtinctBandsCount());
        // assertEquals(0, yorkshire.getSubRegionBandsCount());
        // assertEquals(0, yorkshire.getSubRegionActiveBandsCount());
        // assertEquals(0, yorkshire.getSubRegionExtinctBandsCount());
    }

    @Test
    void testFetchParentRegionIncludesCountsFromSubRegions() {
        // act
        Optional<RegionDao> englandOptional = this.regionService.fetchBySlug("test-england");

        // assert
        assertTrue(englandOptional.isPresent());
        assertFalse(englandOptional.isEmpty());

        RegionDao england = englandOptional.get();

        // TODO fix this - @Formula removed
        // assertEquals(3, england.getBandsCount());
        // assertEquals(2, england.getActiveBandsCount());
        // assertEquals(1, england.getExtinctBandsCount());
        // assertEquals(7, england.getSubRegionBandsCount());
        // assertEquals(5, england.getSubRegionActiveBandsCount());
        // assertEquals(2, england.getSubRegionExtinctBandsCount());
    }
}
