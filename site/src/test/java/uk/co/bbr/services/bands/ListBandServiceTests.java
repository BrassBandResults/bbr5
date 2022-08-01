package uk.co.bbr.services.bands;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.band.BandService;
import uk.co.bbr.services.band.dto.BandListBandDto;
import uk.co.bbr.services.band.dto.BandListDto;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.dao.BbrUserDao;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=list-band-tests-h2", "spring.datasource.url=jdbc:h2:mem:list-band-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username="member_user", roles= { "BBR_MEMBER" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ListBandServiceTests implements LoginMixin {
    @Autowired private BandService bandService;
    @Autowired private RegionService regionService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        this.securityService.createUser(TestUser.TEST_ADMIN.getUsername(), TestUser.TEST_ADMIN.getPassword(), TestUser.TEST_ADMIN.getEmail());
        BbrUserDao localUser = this.securityService.authenticate(TestUser.TEST_ADMIN.getUsername(), TestUser.TEST_ADMIN.getPassword());
        String jwtEncoded = this.jwtService.createJwt(localUser);
        DecodedJWT jwt = this.jwtService.verifyJwt(jwtEncoded);

        // Token is valid, set auth context
        final Authentication auth = this.jwtService.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        RegionDao midlands = this.regionService.findBySlug("midlands");
        RegionDao yorkshire = this.regionService.findBySlug("yorkshire");
        RegionDao northWest = this.regionService.findBySlug("north-west");
        RegionDao wales = this.regionService.findBySlug("wales");
        RegionDao norway = this.regionService.findBySlug("norway");
        RegionDao denmark = this.regionService.findBySlug("denmark");

        this.bandService.create("Abercrombie Primary School Community", midlands);
        this.bandService.create("Black Dyke Band", yorkshire);
        this.bandService.create("Accrington Borough", northWest);
        this.bandService.create("Rothwell Temperance", yorkshire);
        this.bandService.create("48th Div. R.E. T.A", midlands);
        this.bandService.create("Aalesunds Ungdomsmusikkorps", norway);
        this.bandService.create("Abb Kettleby", midlands);
        this.bandService.create("Acceler8", northWest);
        this.bandService.create("Aberllefenni", wales);
        this.bandService.create("Aalborg Brass Band", denmark);
        this.bandService.create("102 (Cheshire) Transport Column R.A.S.C. (T.A.)", northWest);
    }

    @Test
    void testListBandsByLetterAWorksCorrectly() {
        // act
        BandListDto bandsStartWithA = this.bandService.listBandsStartingWith("A");

        // assert
        assertEquals(7, bandsStartWithA.getReturnedBandsCount());
        assertEquals(11, bandsStartWithA.getAllBandsCount());
        assertEquals("A", bandsStartWithA.getSearchPrefix());
        assertEquals(7, bandsStartWithA.getReturnedBands().size());

        assertEquals("Aalborg Brass Band", bandsStartWithA.getReturnedBands().get(0).getName());
        assertEquals("Aalesunds Ungdomsmusikkorps", bandsStartWithA.getReturnedBands().get(1).getName());
        assertEquals("Abb Kettleby", bandsStartWithA.getReturnedBands().get(2).getName());
        assertEquals("Abercrombie Primary School Community", bandsStartWithA.getReturnedBands().get(3).getName());
        assertEquals("Aberllefenni", bandsStartWithA.getReturnedBands().get(4).getName());
        assertEquals("Acceler8", bandsStartWithA.getReturnedBands().get(5).getName());
        assertEquals("Accrington Borough", bandsStartWithA.getReturnedBands().get(6).getName());
    }

    @Test
    void testListBandsByLetterBWorksCorrectly() {
        // act
        BandListDto bandsStartWithB = this.bandService.listBandsStartingWith("B");

        // assert
        assertEquals(1, bandsStartWithB.getReturnedBandsCount());
        assertEquals(11, bandsStartWithB.getAllBandsCount());
        assertEquals("B", bandsStartWithB.getSearchPrefix());
        assertEquals(1, bandsStartWithB.getReturnedBands().size());

        assertEquals("Black Dyke Band", bandsStartWithB.getReturnedBands().get(0).getName());
        assertEquals("yorkshire", bandsStartWithB.getReturnedBands().get(0).getRegionSlug());
        assertEquals("Yorkshire", bandsStartWithB.getReturnedBands().get(0).getRegionName());
        assertEquals("england", bandsStartWithB.getReturnedBands().get(0).getRegionFlagCode());
        assertEquals(0, bandsStartWithB.getReturnedBands().get(0).getContestCount());
    }

    @Test
    void testListAllBandsWorksSuccessfully() {
        // act
        BandListDto allBands = this.bandService.listBandsStartingWith("ALL");

        // assert
        assertEquals(11, allBands.getReturnedBandsCount());
        assertEquals(11, allBands.getAllBandsCount());
        assertEquals("ALL", allBands.getSearchPrefix());
        assertEquals(11, allBands.getReturnedBands().size());

        assertEquals("102 (Cheshire) Transport Column R.A.S.C. (T.A.)", allBands.getReturnedBands().get(0).getName());
        assertEquals("48th Div. R.E. T.A", allBands.getReturnedBands().get(1).getName());
        assertEquals("Aalborg Brass Band", allBands.getReturnedBands().get(2).getName());
        assertEquals("Aalesunds Ungdomsmusikkorps", allBands.getReturnedBands().get(3).getName());
        assertEquals("Abb Kettleby", allBands.getReturnedBands().get(4).getName());
        assertEquals("Abercrombie Primary School Community", allBands.getReturnedBands().get(5).getName());
        assertEquals("Aberllefenni", allBands.getReturnedBands().get(6).getName());
        assertEquals("Acceler8", allBands.getReturnedBands().get(7).getName());
        assertEquals("Accrington Borough", allBands.getReturnedBands().get(8).getName());
        assertEquals("Black Dyke Band", allBands.getReturnedBands().get(9).getName());
        assertEquals("Rothwell Temperance", allBands.getReturnedBands().get(10).getName());
    }

    @Test
    void testListBandsByNumberWorksSuccessfully() {
        // act
        BandListDto bandsStartingWithNumber = this.bandService.listBandsStartingWith("0");

        // assert
        assertEquals(2, bandsStartingWithNumber.getReturnedBandsCount());
        assertEquals(11, bandsStartingWithNumber.getAllBandsCount());
        assertEquals("0", bandsStartingWithNumber.getSearchPrefix());
        assertEquals(2, bandsStartingWithNumber.getReturnedBands().size());

        List<BandListBandDto> bands = bandsStartingWithNumber.getReturnedBands();

        // TODO check that results are correct order
        assertEquals("102 (Cheshire) Transport Column R.A.S.C. (T.A.)", bands.get(0).getName());
        assertEquals("north-west", bands.get(0).getRegionSlug());
        assertEquals("North West", bands.get(0).getRegionName());
        assertEquals("england", bands.get(0).getRegionFlagCode());
        assertEquals(0, bands.get(0).getContestCount());
        assertEquals("48th Div. R.E. T.A", bands.get(1).getName());
        assertEquals("midlands", bands.get(1).getRegionSlug());
        assertEquals("Midlands", bands.get(1).getRegionName());
        assertEquals("england", bands.get(1).getRegionFlagCode());
        assertEquals(0, bands.get(1).getContestCount());

    }
}

