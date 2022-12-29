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

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-alt-name-tests-h2", "spring.datasource.url=jdbc:h2:mem:band-alt-name-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
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

        BandDao band = this.bandService.create("Rothwell Temperance");
        BandPreviousNameDao newPreviousName = new BandPreviousNameDao();
        newPreviousName.setOldName("Rothwell Temperance B Band");

        // act
        BandPreviousNameDao previousName = this.bandService.createPreviousName(band, newPreviousName);

        // assert
        assertEquals("Rothwell Temperance", previousName.getBand().getName());
        assertEquals("Rothwell Temperance B Band", previousName.getOldName());

        logoutTestUser();
    }
}


