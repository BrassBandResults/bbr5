package uk.co.bbr.web.access;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandRelationshipService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandRelationshipDao;
import uk.co.bbr.services.events.ContestEventService;
import uk.co.bbr.services.groups.ContestGroupService;
import uk.co.bbr.services.events.ContestResultService;
import uk.co.bbr.services.contests.ContestService;
import uk.co.bbr.services.tags.ContestTagService;
import uk.co.bbr.services.contests.dao.ContestDao;
import uk.co.bbr.services.events.dao.ContestEventDao;
import uk.co.bbr.services.groups.dao.ContestGroupDao;
import uk.co.bbr.services.tags.dao.ContestTagDao;
import uk.co.bbr.services.people.PersonRelationshipService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.people.dao.PersonRelationshipDao;
import uk.co.bbr.services.pieces.PieceService;
import uk.co.bbr.services.pieces.dao.PieceDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.services.venues.VenueService;
import uk.co.bbr.services.venues.dao.VenueDao;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=public-pages-web-tests-admin-h2", "spring.datasource.url=jdbc:h2:mem:public-pages-web-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicPagesTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private BandService bandService;
    @Autowired private BandAliasService bandAliasService;
    @Autowired private BandRelationshipService bandRelationshipService;
    @Autowired private ContestService contestService;
    @Autowired private ContestEventService contestEventService;
    @Autowired private ContestGroupService contestGroupService;
    @Autowired private ContestTagService contestTagService;
    @Autowired private ContestResultService contestResultService;
    @Autowired private PersonService personService;
    @Autowired private PersonRelationshipService personRelationshipService;
    @Autowired private RegionService regionService;
    @Autowired private PieceService pieceService;
    @Autowired private VenueService venueService;
    @Autowired private JwtService jwtService;
    @Autowired private RestTemplate restTemplate;
    @LocalServerPort private int port;

    @BeforeAll
    void setupBands() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        Optional<RegionDao> yorkshire = this.regionService.fetchBySlug("yorkshire");
        assertTrue(yorkshire.isPresent());

        BandDao rtb = this.bandService.create("Rothwell Temperance", yorkshire.get());
        BandAliasDao alias = this.bandAliasService.createAlias(rtb, "Temps");
        assertEquals(1, alias.getId());

        BandDao wallaceArnold = this.bandService.create("Wallace Arnold Rothwell", yorkshire.get());
        BandRelationshipDao bandrelationship = new BandRelationshipDao();
        bandrelationship.setLeftBand(wallaceArnold);
        bandrelationship.setRightBand(rtb);
        bandrelationship.setRelationship(this.bandRelationshipService.fetchIsParentOfRelationship());
        bandrelationship = this.bandRelationshipService.createRelationship(bandrelationship);
        assertEquals(1, bandrelationship.getId());

        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao gordonRoberts = this.personService.create("Roberts", "Gordon");
        PersonRelationshipDao personRelationship = new PersonRelationshipDao();
        personRelationship.setLeftPerson(gordonRoberts);
        personRelationship.setRightPerson(davidRoberts);
        personRelationship.setRelationship(this.personRelationshipService.fetchTypeByName("relationship.person.is-father-of"));
        personRelationship = this.personRelationshipService.createRelationship(personRelationship);
        assertEquals(1, personRelationship.getId());

        VenueDao stGeorges = this.venueService.create("St George's Hall");
        assertEquals("st-george-s-hall", stGeorges.getSlug());

        ContestDao yorkshireArea = this.contestService.create("Yorkshire Area");
        ContestEventDao yorkshireArea2010 = this.contestEventService.create(yorkshireArea, LocalDate.of(2000, 3, 1));
        yorkshireArea2010.setVenue(stGeorges);
        yorkshireArea2010 = this.contestEventService.update(yorkshireArea2010);
        this.contestResultService.addResult(yorkshireArea2010, "1", rtb, davidRoberts);

        ContestGroupDao yorkshireGroup = this.contestGroupService.create("Yorkshire Group");
        yorkshireArea = this.contestService.addContestToGroup(yorkshireArea, yorkshireGroup);

        ContestTagDao yorkshireTag = this.contestTagService.create("Yorkshire");
        yorkshireArea = this.contestService.addContestTag(yorkshireArea, yorkshireTag);
        yorkshireGroup = this.contestGroupService.addGroupTag(yorkshireGroup, yorkshireTag);

        PieceDao contestMusic = this.pieceService.create("Contest Music");

        logoutTestUser();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/bands/not-a-band",
            "/bands/not-a-band/whits",
            "/contests/not-a-contest",
            "/contests/not-a-contest/2000-03-01",
            "/contests/NOT-A-GROUP",
            "/people/not-a-person",
            "/people/not-a-person/whits",
            "/people/not-a-person/pieces",
            "/pieces/not-a-piece",
            "/regions/not-a-region",
            "/regions/not-a-region/contests",
            "/regions/not-a-region/links",
            "/tags/not-a-tag",
            "/venues/not-a-venue",
    })
    void testInvalidSlugPagesFailAsExpectedWhenNotLoggedIn(String offset) {
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class, () -> this.restTemplate.getForObject("http://localhost:" + this.port + offset, String.class));
        assertTrue(Objects.requireNonNull(ex.getMessage()).contains("404"));  // Page Doesn't Exist
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/",
            "/about-us",
            "/bands/",
            "/bands/R",
            "/bands/0",
            "/bands/ALL",
            "/bands/MAP",
            "/bands/rothwell-temperance",
            "/bands/rothwell-temperance/whits",
            "/contests",
            "/contests/Y",
            "/contests/ALL",
            "/contests/yorkshire-area",
            "/contests/yorkshire-area/2000-03-01",
            "/contests/YORKSHIRE-GROUP",
            "/contest-groups",
            "/contest-groups/Y",
            "/contest-groups/ALL",
            "/faq",
            "/people",
            "/people/R",
            "/people/david-roberts",
            "/people/david-roberts/whits",
            "/people/david-roberts/pieces",
            "/pieces",
            "/pieces/C",
            "/pieces/0",
            "/pieces/ALL",
            "/pieces/contest-music",
            "/privacy",
            "/regions",
            "/regions/yorkshire",
            "/regions/yorkshire/contests",
            "/regions/yorkshire/links",
            "/regions/yorkshire/championship/bands.json",
            "/statistics",
            "/tags",
            "/tags/Y",
            "/tags/ALL",
            "/tags/yorkshire",
            "/venues",
            "/venues/S",
            "/venues/0",
            "/venues/ALL",
            "/venues/MAP",
            "/venues/st-george-s-hall",
    })
    void testPagesReturnSuccessfullyWhenNotLoggedIn(String offset) {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + offset, String.class);
        assertNotNull(response);
        assertFalse(response.contains("<h2>Sign In</h2>")); // Page doesn't require login
        assertFalse(response.contains("Page not found"));  // Page Exists
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/bands/COMPARE",
            "/bands/COMPARE/rothwell-temperance",
            "/bands/COMPARE/rothwell-temperance/wallace-arnold-rothwell",
            "/bands/WINNERS",
            "/bands/rothwell-temperance/yorkshire-area",
            "/bands/rothwell-temperance/YORKSHIRE-GROUP",
            "/bands/rothwell-temperance/tag/yorkshire",
            "/bands/rothwell-temperance/edit",
            "/bands/rothwell-temperance/edit-aliases",
            "/bands/rothwell-temperance/edit-aliases/1/show",
            "/bands/rothwell-temperance/edit-aliases/1/hide",
            "/bands/rothwell-temperance/edit-aliases/1/edit-dates",
            "/bands/rothwell-temperance/edit-aliases/1/delete",
            "/bands/rothwell-temperance/edit-rehearsals",
            "/bands/rothwell-temperance/edit-relationships",
            "/bands/rothwell-temperance/edit-relationships/1/delete",
            "/contests/yorkshire-area/own-choice",
            "/contests/yorkshire-area/wins",
            "/contests/YORKSHIRE-GROUP/years",
            "/contests/YORKSHIRE-GROUP/2000",
            "/create/tag",
            "/lookup/band/data.json?s=abc",
            "/lookup/contest/data.json?s=abc",
            "/lookup/person/data.json?s=abc",
            "/people/david-roberts/edit",
            "/people/david-roberts/edit-aliases",
            "/people/david-roberts/edit-aliases/1/hide",
            "/people/david-roberts/edit-aliases/1/show",
            "/people/david-roberts/edit-aliases/1/delete",
            "/people/david-roberts/edit-relationships",
            "/people/david-roberts/edit-relationships/1/delete",
            "/people/COMPARE-CONDUCTORS",
            "/people/COMPARE-CONDUCTORS/david-roberts",
            "/people/COMPARE-CONDUCTORS/david-roberts/gordon-roberts",
            "/people/WINNERS",
            "/people/WINNERS/before/1950",
            "/people/WINNERS/after/1950",
            "/pieces/BY-SECTION/championship",
            "/pieces/BEST-OWN-CHOICE",
            "/tags/yorkshire/delete",
            "/regions/yorkshire/winners",
            "/venues/st-george-s-hall/yorkshire-area",
            "/venues/st-george-s-hall/years",
            "/venues/st-george-s-hall/years/2000",
            "/years",
            "/years/2000",
    })
    void testPagesFailWhenNotLoggedIn(String offset) {
        String response = this.restTemplate.getForObject("http://localhost:" + this.port + offset, String.class);
        assertNotNull(response);
        assertTrue(response.contains("<h2>Sign In</h2>")); // page requires login
        assertFalse(response.contains("Page not found")); // page exists
    }
}

