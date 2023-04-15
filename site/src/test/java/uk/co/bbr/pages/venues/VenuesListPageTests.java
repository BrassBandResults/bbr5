package uk.co.bbr.pages.venues;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.contests.dto.ContestListDto;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dto.VenueListDto;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=venue-list-page-service-tests-h2", "spring.datasource.url=jdbc:h2:mem:venue-list-page-service-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VenuesListPageTests implements LoginMixin {

    @Autowired private VenueService venueService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupContests() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        this.venueService.create("Ab Venue 1");
        this.venueService.create("Ac Venue 2");
        this.venueService.create("Bx Venue 3");
        this.venueService.create("Bx Venue 4");
        this.venueService.create("Cx Venue 5");
        this.venueService.create("Aa Venue 6");

        logoutTestUser();
    }


    @Test
    void testFetchContestListWithPrefixWorksSuccessfullyForContests() {
        // act
        VenueListDto pageData = this.venueService.listVenuesStartingWith("A");

        // assert
        assertEquals("A", pageData.getSearchPrefix());
        assertEquals(6, pageData.getAllVenuesCount());
        assertEquals(3, pageData.getReturnedVenuesCount());
        assertEquals(3, pageData.getReturnedVenues().size());
        assertEquals("Aa Venue 6", pageData.getReturnedVenues().get(0).getName());
        assertEquals(0, pageData.getReturnedVenues().get(0).getEventCount());

        assertEquals("Ab Venue 1", pageData.getReturnedVenues().get(1).getName());
        assertEquals(0, pageData.getReturnedVenues().get(1).getEventCount());

        assertEquals("Ac Venue 2", pageData.getReturnedVenues().get(2).getName());
        assertEquals(0, pageData.getReturnedVenues().get(2).getEventCount());
    }

    @Test
    void testFetchContestListLAllWorksSuccessfullyForContests() {
        // act
        VenueListDto pageData = this.venueService.listVenuesStartingWith("ALL");

        // assert
        assertEquals("ALL", pageData.getSearchPrefix());
        assertEquals(6, pageData.getAllVenuesCount());
        assertEquals(6, pageData.getReturnedVenuesCount());

        assertEquals(6, pageData.getReturnedVenues().size());
        assertEquals("Aa Venue 6", pageData.getReturnedVenues().get(0).getName());
        assertEquals(0, pageData.getReturnedVenues().get(0).getEventCount());

        assertEquals("Ab Venue 1", pageData.getReturnedVenues().get(1).getName());
        assertEquals(0, pageData.getReturnedVenues().get(1).getEventCount());

        assertEquals("Ac Venue 2", pageData.getReturnedVenues().get(2).getName());
        assertEquals(0, pageData.getReturnedVenues().get(2).getEventCount());

        assertEquals("Bx Venue 3", pageData.getReturnedVenues().get(3).getName());
        assertEquals(0, pageData.getReturnedVenues().get(3).getEventCount());

        assertEquals("Bx Venue 4", pageData.getReturnedVenues().get(4).getName());
        assertEquals(0, pageData.getReturnedVenues().get(4).getEventCount());

        assertEquals("Cx Venue 5", pageData.getReturnedVenues().get(5).getName());
        assertEquals(0, pageData.getReturnedVenues().get(5).getEventCount());
    }
}



