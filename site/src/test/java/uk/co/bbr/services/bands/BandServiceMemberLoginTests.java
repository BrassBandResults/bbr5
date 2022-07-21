package uk.co.bbr.services.bands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.band.BandService;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.web.LoginMixin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=band-tests-h2", "spring.datasource.url=jdbc:h2:mem:region-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@WithMockUser(username="member_user", roles= { "BBR_MEMBER" })
public class BandServiceMemberLoginTests implements LoginMixin {

    @Autowired
    private BandService bandService;

    @Test
    void testCreateBandWorksSuccessfullyWithJustName() {
        // act
        BandDao createdBand = this.bandService.create("Rothwell Temperance Band");

        // assert
        assertNotNull(createdBand.getId());
        assertNotNull(createdBand.getCreated());
        assertEquals(1, createdBand.getCreatedBy());
        assertNotNull(createdBand.getUpdated());
        assertEquals(1, createdBand.getUpdatedBy());
        assertEquals("Rothwell Temperance Band", createdBand.getName());
        assertEquals("Unknown", createdBand.getRegion().getName());
    }
}
