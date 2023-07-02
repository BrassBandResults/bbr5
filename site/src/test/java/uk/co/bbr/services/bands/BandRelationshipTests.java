package uk.co.bbr.services.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:bands-relationship-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BandRelationshipTests implements LoginMixin {
    @Autowired private BandService bandService;
    @Autowired private BandRelationshipService bandRelationshipService;
    @Autowired private SectionService sectionService;
    @Autowired private RegionService regionService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao blackDyke = this.bandService.create("Black Dyke Band", yorkshire);
        blackDyke.setSection(this.sectionService.fetchBySlug("championship").get());
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
        rothwellOld.setOldId("987654");
        this.bandService.update(rothwellOld);

        logoutTestUser();
    }

    @Test
    void testCreateBandRelationshipWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao rothwellTemperance = this.bandService.fetchBySlug("rothwell-temperance").get();
        BandDao wallaceArnold = this.bandService.fetchBySlug("wallace-arnold-rothwell-band").get();

        BandRelationshipDao newRelationship = new BandRelationshipDao();
        newRelationship.setLeftBand(wallaceArnold);
        newRelationship.setRightBand(rothwellTemperance);
        newRelationship.setRelationship(this.bandRelationshipService.fetchIsParentOfRelationship());

        // act
        BandRelationshipDao relationship = this.bandRelationshipService.createRelationship(newRelationship);

        // assert
        assertEquals("Rothwell Temperance", relationship.getRightBandName());
        assertEquals("Wallace Arnold (Rothwell) Band", relationship.getLeftBandName());
        assertEquals("Rothwell Temperance", relationship.getRightBand().getName());
        assertEquals("Wallace Arnold (Rothwell) Band", relationship.getLeftBand().getName());
        assertEquals("relationship.band.is-parent-of", relationship.getRelationship().getName());
        assertEquals("relationship.band.has-parent-of", relationship.getRelationship().getReverseName());

        logoutTestUser();
    }

    @Test
    void testCreateBandRelationshipWithDatesWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao rothwellTemperance = this.bandService.fetchBySlug("rothwell-temperance").get();
        BandDao wallaceArnold = this.bandService.fetchBySlug("wallace-arnold-rothwell-band").get();

        BandRelationshipDao newRelationship = new BandRelationshipDao();
        newRelationship.setLeftBand(wallaceArnold);
        newRelationship.setRightBand(rothwellTemperance);
        newRelationship.setRelationship(this.bandRelationshipService.fetchIsParentOfRelationship());
        newRelationship.setStartDate(LocalDate.of(2020, 1, 1));
        newRelationship.setEndDate(LocalDate.of(2022, 12, 31));

        // act
        BandRelationshipDao relationship = this.bandRelationshipService.createRelationship(newRelationship);

        // assert
        assertEquals("Rothwell Temperance", relationship.getRightBandName());
        assertEquals("Wallace Arnold (Rothwell) Band", relationship.getLeftBandName());
        assertEquals("Rothwell Temperance", relationship.getRightBand().getName());
        assertEquals("Wallace Arnold (Rothwell) Band", relationship.getLeftBand().getName());
        assertEquals("relationship.band.is-parent-of", relationship.getRelationship().getName());
        assertEquals("relationship.band.has-parent-of", relationship.getRelationship().getReverseName());
        assertEquals(LocalDate.of(2020, 1, 1), newRelationship.getStartDate());
        assertEquals(LocalDate.of(2022, 12, 31), newRelationship.getEndDate());

        logoutTestUser();
    }

    @Test
    void testCreateBandRelationshipWithStartDateAfterEndDateFails() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao rothwellTemperance = this.bandService.fetchBySlug("rothwell-temperance").get();
        BandDao wallaceArnold = this.bandService.fetchBySlug("wallace-arnold-rothwell-band").get();

        BandRelationshipDao newRelationship = new BandRelationshipDao();
        newRelationship.setLeftBand(wallaceArnold);
        newRelationship.setRightBand(rothwellTemperance);
        newRelationship.setRelationship(this.bandRelationshipService.fetchIsParentOfRelationship());
        newRelationship.setStartDate(LocalDate.of(2020, 1, 2));
        newRelationship.setEndDate(LocalDate.of(2020, 1, 1));

        // act
        ValidationException ex = assertThrows(ValidationException.class, () -> this.bandRelationshipService.createRelationship(newRelationship));

        // assert
        assertEquals("Start date can't be after end date", ex.getMessage());

        logoutTestUser();
    }
}

