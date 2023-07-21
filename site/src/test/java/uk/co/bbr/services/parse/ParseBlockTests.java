package uk.co.bbr.services.parse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.BandAliasService;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandAliasDao;
import uk.co.bbr.services.results.ParseResultService;
import uk.co.bbr.services.results.ParseService;
import uk.co.bbr.services.results.dto.ParseResultDto;
import uk.co.bbr.services.results.types.ParseOutcome;
import uk.co.bbr.services.people.PersonAliasService;
import uk.co.bbr.services.people.PersonService;
import uk.co.bbr.services.people.dao.PersonAliasDao;
import uk.co.bbr.services.people.dao.PersonDao;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:parse-block-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseBlockTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ParseResultService parseResultService;
    @Autowired private BandService bandService;
    @Autowired private BandAliasService bandAliasService;
    @Autowired private RegionService regionService;
    @Autowired private PersonService personService;
    @Autowired private PersonAliasService personAliasService;

    @BeforeAll
    void setupData() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao blackDyke = this.bandService.create("Black Dyke Band", yorkshire);
        BandDao rothwell = this.bandService.create("Rothwell Temperance B", yorkshire);
        BandDao wallaceArnold = this.bandService.create("Wallace Arnold (Rothwell) Band", yorkshire);
        BandDao rothwellOld = this.bandService.create("Rothwell Old", yorkshire);
        BandDao suffolkVolunteer = this.bandService.create("11th Suffolk Volunteer Rifles (Sudbury)", yorkshire);
        BandDao briggus = this.bandService.create("Brighouse & Rastrick", yorkshire);

        BandAliasDao rothwellJunior = new BandAliasDao();
        rothwellJunior.setOldName("Rothwell Temperance Junior Band");
        this.bandAliasService.createAlias(rothwell, rothwellJunior);

        PersonDao robertChilds = this.personService.create("Childs", "Robert");
        PersonDao davidChilds = this.personService.create("Childs", "David");
        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao TheoQWhigley = this.personService.create("Whigley", "Theo Q.");
        PersonDao TheoQPWhigley = this.personService.create("Whigley", "Theo Q. P.");

        PersonAliasDao bobChilds = new PersonAliasDao();
        bobChilds.setOldName("Bob Childs");
        this.personAliasService.createAlias(robertChilds, bobChilds);

        logoutTestUser();
    }

    @Test
    void testParseSingleLineBlock() {
        // arrange
        String testEntry = "1. Black Dyke Band, Robert Childs, 5, 123";

        // act
        List<ParseResultDto> parseResult = this.parseResultService.parseBlock(testEntry, LocalDate.now()).getResultLines();

        // assert
        assertEquals(1, parseResult.size());
        assertEquals(ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE, parseResult.get(0).getOutcome());

        assertEquals("1", parseResult.get(0).getRawPosition());
        assertEquals("Black Dyke Band", parseResult.get(0).getRawBandName());
        assertEquals("Robert Childs", parseResult.get(0).getRawConductorName());
        assertEquals(5, parseResult.get(0).getRawDraw());
        assertEquals("123", parseResult.get(0).getRawPoints());

        assertEquals("black-dyke-band", parseResult.get(0).getMatchedBand().getSlug());
        assertEquals("robert-childs", parseResult.get(0).getMatchedConductor().getSlug());
        assertEquals("Childs", parseResult.get(0).getMatchedConductor().getSurname());
        assertEquals("Robert", parseResult.get(0).getMatchedConductor().getFirstNames());

        assertNotNull(parseResult.get(0).buildContestResult(null));
    }

    @Test
    void testParseTwoLineBlock() {
        // arrange
        String testEntry = """
                  1. Black Dyke Band, Robert Childs, 5, 123
                  5. Rothwell Temperance, David Roberts, 26""";

        // act
        List<ParseResultDto> parseResult = this.parseResultService.parseBlock(testEntry, LocalDate.now()).getResultLines();

        // assert
        assertEquals(2, parseResult.size());
        assertEquals(ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE, parseResult.get(0).getOutcome());

        assertEquals("1", parseResult.get(0).getRawPosition());
        assertEquals("Black Dyke Band", parseResult.get(0).getRawBandName());
        assertEquals("Robert Childs", parseResult.get(0).getRawConductorName());
        assertEquals(5, parseResult.get(0).getRawDraw());
        assertEquals("123", parseResult.get(0).getRawPoints());

        assertEquals("black-dyke-band", parseResult.get(0).getMatchedBand().getSlug());
        assertEquals("robert-childs", parseResult.get(0).getMatchedConductor().getSlug());
        assertEquals("Childs", parseResult.get(0).getMatchedConductor().getSurname());
        assertEquals("Robert", parseResult.get(0).getMatchedConductor().getFirstNames());

        assertNotNull(parseResult.get(0).buildContestResult(null));

        assertEquals(ParseOutcome.GREEN_MATCHES_FOUND_IN_DATABASE, parseResult.get(1).getOutcome());
        assertEquals("5", parseResult.get(1).getRawPosition());
        assertEquals("Rothwell Temperance", parseResult.get(1).getRawBandName());
        assertEquals("David Roberts", parseResult.get(1).getRawConductorName());
        assertEquals(26, parseResult.get(1).getRawDraw());
        assertEquals("", parseResult.get(1).getRawPoints());

        assertEquals("rothwell-temperance-b", parseResult.get(1).getMatchedBand().getSlug());
        assertEquals("david-roberts", parseResult.get(1).getMatchedConductor().getSlug());
        assertEquals("Roberts", parseResult.get(1).getMatchedConductor().getSurname());
        assertEquals("David", parseResult.get(1).getMatchedConductor().getFirstNames());

        assertNotNull(parseResult.get(1).buildContestResult(null));
    }
}
