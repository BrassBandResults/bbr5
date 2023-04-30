package uk.co.bbr.services.parse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.BandService;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandPreviousNameDao;
import uk.co.bbr.services.parse.dto.ParseResultDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=parse-tests-h2", "spring.datasource.url=jdbc:h2:mem:parse-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE", "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseTests implements LoginMixin {

    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;
    @Autowired private ParseService parseService;
    @Autowired private BandService bandService;
    @Autowired private RegionService regionService;
    @Autowired private PersonService personService;

    @BeforeAll
    void setupData() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshire = this.regionService.fetchBySlug("yorkshire").get();

        BandDao blackDyke = this.bandService.create("Black Dyke Band", yorkshire);
        BandDao rothwell = this.bandService.create("Rothwell Temperance B", yorkshire);
        BandDao wallaceArnold = this.bandService.create("Wallace Arnold (Rothwell) Band", yorkshire);
        BandDao rothwellOld = this.bandService.create("Rothwell Old", yorkshire);

        BandPreviousNameDao rothwellJunior = new BandPreviousNameDao();
        rothwellJunior.setOldName("Rothwell Temperance Junior Band");
        this.bandService.createPreviousName(rothwell, rothwellJunior);

        PersonDao robertChilds = this.personService.create("Childs", "Robert");
        PersonDao davidChilds = this.personService.create("Childs", "David");
        PersonDao davidRoberts = this.personService.create("Roberts", "David");
        PersonDao johnRoberts = this.personService.create("Roberts", "John");
        PersonDao TheoQWhigley = this.personService.create("Whigley", "Theo Q.");
        PersonDao TheoQPWhigley = this.personService.create("Whigley", "Theo Q. P.");

        PersonAliasDao bobChilds = new PersonAliasDao();
        bobChilds.setOldName("Bob Childs");
        this.personService.createAlternativeName(robertChilds, bobChilds);

        logoutTestUser();
    }

    @Test
    void testParseContestResultLineWithGibberishFails() {
        // arrange
        String testEntry = "Gibberish";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertFalse(parseResult.isParseSuccess());

        assertNull(parseResult.getRawPosition());
        assertNull(parseResult.getRawBandName());
        assertNull(parseResult.getRawConductorName());
        assertNull(parseResult.getRawDraw());
        assertNull(parseResult.getRawPoints());

        assertNull(parseResult.getMatchedBand());
        assertNull(parseResult.getMatchedConductor());

        assertFalse(parseResult.isMatchSuccess());
   }

    @Test
    void testParseContestResultLineWithOldStyleCorrectInputWorks() {
        // arrange
        String testEntry = "1. Black Dyke Band, Robert Childs, 5, 123";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("1", parseResult.getRawPosition());
        assertEquals("Black Dyke Band", parseResult.getRawBandName());
        assertEquals("Robert Childs", parseResult.getRawConductorName());
        assertEquals("5", parseResult.getRawDraw());
        assertEquals("123", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("black-dyke-band", parseResult.getMatchedBand().getSlug());
        assertEquals("robert-childs", parseResult.getMatchedConductor().getSlug());
        assertEquals("Childs", parseResult.getMatchedConductor().getSurname());
        assertEquals("Robert", parseResult.getMatchedConductor().getFirstNames());
    }

    @Test
    void testParseContestResultLinePointsAreOptionalWorks() {
        // arrange
        String testEntry = "5. Rothwell Temperance, David Roberts, 26";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("5", parseResult.getRawPosition());
        assertEquals("Rothwell Temperance", parseResult.getRawBandName());
        assertEquals("David Roberts", parseResult.getRawConductorName());
        assertEquals("26", parseResult.getRawDraw());
        assertEquals("", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("rothwell-temperance-b", parseResult.getMatchedBand().getSlug());
        assertEquals("david-roberts", parseResult.getMatchedConductor().getSlug());
        assertEquals("Roberts", parseResult.getMatchedConductor().getSurname());
        assertEquals("David", parseResult.getMatchedConductor().getFirstNames());
    }

    @Test
    void testParseContestResultLineZeroPositionWorks() {
        // arrange
        String testEntry = "0. Rothwell Temperance, David Roberts, 111";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("0", parseResult.getRawPosition());
        assertEquals("Rothwell Temperance", parseResult.getRawBandName());
        assertEquals("David Roberts", parseResult.getRawConductorName());
        assertEquals("111", parseResult.getRawDraw());
        assertEquals("", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("rothwell-temperance-b", parseResult.getMatchedBand().getSlug());
        assertEquals("david-roberts", parseResult.getMatchedConductor().getSlug());
        assertEquals("Roberts", parseResult.getMatchedConductor().getSurname());
        assertEquals("David", parseResult.getMatchedConductor().getFirstNames());
    }

    @Test
    void testParseContestResultLineMultipleSpacesAreRemovedSuccessfully() {
        // arrange
        String testEntry = "  1.     Black     Dyke     Band    ,     Robert     Childs    ,     5    ,    321  ";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("1", parseResult.getRawPosition());
        assertEquals("Black Dyke Band", parseResult.getRawBandName());
        assertEquals("Robert Childs", parseResult.getRawConductorName());
        assertEquals("5", parseResult.getRawDraw());
        assertEquals("321", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("black-dyke-band", parseResult.getMatchedBand().getSlug());
        assertEquals("robert-childs", parseResult.getMatchedConductor().getSlug());
        assertEquals("Childs", parseResult.getMatchedConductor().getSurname());
        assertEquals("Robert", parseResult.getMatchedConductor().getFirstNames());
    }

    @Test
    void testParseContestResultLinePersonWithInitialNormalisedSuccessfully() {
        // arrange
        String testEntry = "  1.     Black     Dyke     Band    ,     Theo    Q   Whigley    ,     5    ,    321  ";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("1", parseResult.getRawPosition());
        assertEquals("Black Dyke Band", parseResult.getRawBandName());
        assertEquals("Theo Q. Whigley", parseResult.getRawConductorName());
        assertEquals("5", parseResult.getRawDraw());
        assertEquals("321", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("black-dyke-band", parseResult.getMatchedBand().getSlug());
        assertEquals("theo-q-whigley", parseResult.getMatchedConductor().getSlug());
        assertEquals("Whigley", parseResult.getMatchedConductor().getSurname());
        assertEquals("Theo Q.", parseResult.getMatchedConductor().getFirstNames());
    }

    @Test
    void testParseContestResultLinePersonWithDoubleInitialNormalisedSuccessfully() {
        // arrange
        String testEntry = "  1.     Black     Dyke     Band    ,     Theo    Q    P  Whigley    ,     5    ,    321  ";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("1", parseResult.getRawPosition());
        assertEquals("Black Dyke Band", parseResult.getRawBandName());
        assertEquals("Theo Q. P. Whigley", parseResult.getRawConductorName());
        assertEquals("5", parseResult.getRawDraw());
        assertEquals("321", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("black-dyke-band", parseResult.getMatchedBand().getSlug());
        assertEquals("theo-q-p-whigley", parseResult.getMatchedConductor().getSlug());
        assertEquals("Whigley", parseResult.getMatchedConductor().getSurname());
        assertEquals("Theo Q. P.", parseResult.getMatchedConductor().getFirstNames());
    }

    @Test
    void testParseContestResultWithdrawalResultWorks() {
        // arrange
        String testEntry = "W. Wallace Arnold (Rothwell) Band, John Roberts, 13";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("W", parseResult.getRawPosition());
        assertEquals("Wallace Arnold (Rothwell) Band", parseResult.getRawBandName());
        assertEquals("John Roberts", parseResult.getRawConductorName());
        assertEquals("13", parseResult.getRawDraw());
        assertEquals("", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("wallace-arnold-rothwell-band", parseResult.getMatchedBand().getSlug());
        assertEquals("john-roberts", parseResult.getMatchedConductor().getSlug());
        assertEquals("Roberts", parseResult.getMatchedConductor().getSurname());
        assertEquals("John", parseResult.getMatchedConductor().getFirstNames());
    }

    @Test
    void testParseContestResultDisqualifiedResultWorks() {
        // arrange
        String testEntry = "D. Wallace Arnold (Rothwell) Band, John Roberts, 11";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("D", parseResult.getRawPosition());
        assertEquals("Wallace Arnold (Rothwell) Band", parseResult.getRawBandName());
        assertEquals("John Roberts", parseResult.getRawConductorName());
        assertEquals("11", parseResult.getRawDraw());
        assertEquals("", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("wallace-arnold-rothwell-band", parseResult.getMatchedBand().getSlug());
        assertEquals("john-roberts", parseResult.getMatchedConductor().getSlug());
        assertEquals("Roberts", parseResult.getMatchedConductor().getSurname());
        assertEquals("John", parseResult.getMatchedConductor().getFirstNames());
    }

    @Test
    void testParseContestResultInvalidResultLetterFails() {
        // arrange
        String testEntry = "Q. Wallace Arnold (Rothwell) Band, John Roberts, 11";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertFalse(parseResult.isParseSuccess());

        assertNull(parseResult.getRawPosition());
        assertNull(parseResult.getRawBandName());
        assertNull(parseResult.getRawConductorName());
        assertNull(parseResult.getRawDraw());
        assertNull(parseResult.getRawPoints());

        assertFalse(parseResult.isMatchSuccess());

        assertNull(parseResult.getMatchedBand());
        assertNull(parseResult.getMatchedConductor());
    }

    @Test
    void testParseContestResultConductorCanBeFoundByAlias() {
        // arrange
        String testEntry = "11. Wallace Arnold (Rothwell) Band, Bob Childs, 321";

        // act
        ParseResultDto parseResult = this.parseService.parseLine(testEntry, LocalDate.now());

        // assert
        assertTrue(parseResult.isParseSuccess());

        assertEquals("11", parseResult.getRawPosition());
        assertEquals("Wallace Arnold (Rothwell) Band", parseResult.getRawBandName());
        assertEquals("Bob Childs", parseResult.getRawConductorName());
        assertEquals("321", parseResult.getRawDraw());
        assertEquals("", parseResult.getRawPoints());

        assertTrue(parseResult.isMatchSuccess());

        assertEquals("wallace-arnold-rothwell-band", parseResult.getMatchedBand().getSlug());
        assertEquals("robert-childs", parseResult.getMatchedConductor().getSlug());
        assertEquals("Childs", parseResult.getMatchedConductor().getSurname());
        assertEquals("Robert", parseResult.getMatchedConductor().getFirstNames());
    }
}
