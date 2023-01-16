package uk.co.bbr.services.venues;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=create-venue-tests-h2", "spring.datasource.url=jdbc:h2:mem:create-venue-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class CreateVenueServiceTests implements LoginMixin {

    @Autowired private VenueService venueService;
    @Autowired private RegionService regionService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreateVenueByNameWorksSuccessfully() throws AuthenticationFailedException {
        // act
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao venue = this.venueService.create("  New   Venue   ");

        // assert
        assertEquals("New Venue", venue.getName());
        assertEquals("new-venue", venue.getSlug());
        assertNull(venue.getNotes());
        assertNull(venue.getLatitude());
        assertNull(venue.getLongitude());
        assertFalse(venue.isExact());
        assertNull(venue.getRegion());
        assertNull(venue.getMapper());

        logoutTestUser();
    }

    @Test
    void testCreateVenueWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        VenueDao newVenue = new VenueDao();
        newVenue.setName(" Another   New   Venue  ");
        newVenue.setNotes("  Notes  ");
        newVenue.setLatitude(" 123 ");
        newVenue.setLongitude(" 456 ");
        newVenue.setExact(true);
        newVenue.setRegion(this.regionService.fetchBySlug("yorkshire").get());
        newVenue.setMapper(this.securityService.getCurrentUser());

        // act
        VenueDao venue = this.venueService.create(newVenue);

        // assert
        assertEquals("Another New Venue", venue.getName());
        assertEquals("another-new-venue", venue.getSlug());
        assertEquals("Notes", venue.getNotes());
        assertEquals("123", venue.getLatitude());
        assertEquals("456", venue.getLongitude());
        assertTrue(venue.isExact());
        assertEquals("Yorkshire", venue.getRegion().getName());
        assertEquals(this.securityService.getCurrentUser().getUsercode(), venue.getMapper().getUsercode());

        logoutTestUser();
    }
}
