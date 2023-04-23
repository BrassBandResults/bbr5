package uk.co.bbr.services.bands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-alt-name-tests-h2", "spring.datasource.url=jdbc:h2:mem:band-alt-name-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
class BandPreviousNameTests implements LoginMixin {
    @Autowired private BandService bandService;
    @Autowired private SectionService sectionService;
    @Autowired private RegionService regionService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreateBandPreviousNameWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.create("Rothwell Temperance 1");
        BandPreviousNameDao newPreviousName = new BandPreviousNameDao();
        newPreviousName.setOldName("Rothwell Temperance B Band");

        // act
        BandPreviousNameDao previousName = this.bandService.createPreviousName(band, newPreviousName);

        // assert
        assertEquals("Rothwell Temperance 1", previousName.getBand().getName());
        assertEquals("Rothwell Temperance B Band", previousName.getOldName());
        assertFalse(previousName.isHidden());
        assertNull(previousName.getStartDate());
        assertNull(previousName.getEndDate());

        logoutTestUser();
    }

    @Test
    void testCreateBandPreviousNameWithStartAndEndDateWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.create("Rothwell Temperance 2");
        BandPreviousNameDao newPreviousName = new BandPreviousNameDao();
        newPreviousName.setOldName("Rothwell Temperance B Band");
        newPreviousName.setStartDate(LocalDate.of(2020, 1, 1));
        newPreviousName.setEndDate(LocalDate.of(2022, 12, 31));
        newPreviousName.setHidden(true);

        // act
        BandPreviousNameDao previousName = this.bandService.createPreviousName(band, newPreviousName);

        // assert
        assertEquals("Rothwell Temperance 2", previousName.getBand().getName());
        assertEquals("Rothwell Temperance B Band", previousName.getOldName());
        assertEquals(LocalDate.of(2020, 1, 1), previousName.getStartDate());
        assertEquals(LocalDate.of(2022, 12, 31), previousName.getEndDate());
        assertTrue(previousName.isHidden());

        logoutTestUser();
    }

    @Test
    void testStartDateCantBeAfterEndDate() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.create("Rothwell Temperance 3");
        BandPreviousNameDao newPreviousName = new BandPreviousNameDao();
        newPreviousName.setOldName("Rothwell Temperance B Band");
        newPreviousName.setStartDate(LocalDate.of(2020, 1, 2));
        newPreviousName.setEndDate(LocalDate.of(2020, 1, 1));
        newPreviousName.setHidden(true);

        // act
        ValidationException ex = assertThrows(ValidationException.class, () -> this.bandService.createPreviousName(band, newPreviousName));

        // assert
        assertEquals("Start date can't be after end date", ex.getMessage());

        logoutTestUser();
    }
}


