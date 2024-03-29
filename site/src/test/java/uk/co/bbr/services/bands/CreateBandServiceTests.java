package uk.co.bbr.services.bands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.sections.SectionService;
import uk.co.bbr.services.sections.dao.SectionDao;
import uk.co.bbr.services.security.JwtService;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.services.security.ex.AuthenticationFailedException;
import uk.co.bbr.web.LoginMixin;
import uk.co.bbr.web.security.support.TestUser;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:bands-create-band-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class CreateBandServiceTests implements LoginMixin {

    @Autowired private BandService bandService;
    @Autowired private RegionService regionService;
    @Autowired private SectionService sectionService;
    @Autowired private SecurityService securityService;
    @Autowired private JwtService jwtService;

    @Test
    void testCreateBandWorksSuccessfullyWithJustName() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        // act
        BandDao createdBand = this.bandService.create("Black Dyke Band");

        // assert
        assertNotNull(createdBand.getId());
        assertNotNull(createdBand.getCreated());
        assertNotNull(createdBand.getUpdated());
        assertEquals("Black Dyke Band", createdBand.getName());
        assertEquals("black-dyke-band", createdBand.getSlug());
        assertEquals("Unknown", createdBand.getRegion().getName());

        logoutTestUser();
    }

    @Test
    void testCreateBandWithMultipleSpacesInNameCompressesSpaces() throws AuthenticationFailedException {
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao createdBand = this.bandService.create("  Roth well  Temperance   Band   ");
        assertEquals("Roth well Temperance Band", createdBand.getName());
        assertEquals("roth-well-temperance-band", createdBand.getSlug());

        logoutTestUser();
    }

    @Test
    void testCreateBandWithRegionWorkSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao northWestRegion = this.regionService.fetchBySlug("north-west").get();

        // act
        BandDao band = this.bandService.create("Foden's", northWestRegion);

        // assert
        assertEquals("Foden's", band.getName());
        assertEquals("foden-s", band.getSlug());
        assertEquals("North West", band.getRegion().getName());

        logoutTestUser();
    }

    @Test
    void testCreateBandFromObjectWorksSuccessfully() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        RegionDao yorkshireRegion = this.regionService.fetchBySlug("yorkshire").get();
        SectionDao championshipSection = this.sectionService.fetchBySlug("championship").get();

        BandDao newBand = new BandDao();
        newBand.setName("  Yorkshire Building Society Band  Of Yorkshire formerly Hammonds Sauce Works Band ");
        newBand.setRegion(yorkshireRegion);
        newBand.setLatitude(" 53.792614944591655 ");
        newBand.setLongitude(" -1.7510766401965498 ");
        newBand.setStartDate(LocalDate.of(1900, 1, 1));
        newBand.setEndDate(LocalDate.of(2000, 12, 31));
        newBand.setMapper(this.securityService.getCurrentUsername());
        newBand.setSection(championshipSection);
        newBand.setNotes("   Test Notes   ");
        newBand.setStatus(BandStatus.EXTINCT);
        newBand.setTwitterName("  @Twitter  ");
        newBand.setWebsite("  http://ybsband.org.uk  ");

        // act
        BandDao createdBand = this.bandService.create(newBand);

        // assert
        assertNotNull(createdBand.getId());
        assertNotNull(createdBand.getCreated());
        assertNotNull(createdBand.getUpdated());
        assertEquals(createdBand.getCreated().getDayOfMonth(), createdBand.getUpdated().getDayOfMonth());
        assertEquals("Yorkshire Building Society Band Of Yorkshire formerly Hammonds Sauce Works Band", createdBand.getName());
        assertEquals("yorkshire-building-society-band-of-yorkshire-forme", createdBand.getSlug());
        assertEquals("Yorkshire", createdBand.getRegion().getName());
        assertEquals(15, createdBand.getLatitude().length());
        assertEquals("53.792614944591", createdBand.getLatitude());
        assertEquals(15, createdBand.getLongitude().length());
        assertEquals("-1.751076640196", createdBand.getLongitude());
        assertEquals(LocalDate.of(1900, 1, 1), createdBand.getStartDate());
        assertEquals(LocalDate.of(2000, 12, 31), createdBand.getEndDate());
        assertEquals(this.securityService.getCurrentUsername(), createdBand.getMapper());
        assertEquals("Championship", createdBand.getSection().getName());
        assertEquals("Test Notes", createdBand.getNotes());
        assertEquals(BandStatus.EXTINCT, createdBand.getStatus());
        assertEquals("Twitter", createdBand.getTwitterName());
        assertEquals("http://ybsband.org.uk", createdBand.getWebsite());

        logoutTestUser();
    }

    @Test
    void testCreatingBandWithDuplicateSlugFails() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = this.bandService.create(" BAND  1 ");

        // act
        ValidationException ex = assertThrows(ValidationException.class, ()-> {this.bandService.create("Band 1");});

        // assert
        assertEquals("Band with slug band-1 already exists.", ex.getMessage());

        logoutTestUser();
    }

    @Test
    void testWhenCreateWithPresentIdFailsAsExpected() throws AuthenticationFailedException {
        // arrange
        loginTestUser(this.securityService, this.jwtService, TestUser.TEST_MEMBER);

        BandDao band = new BandDao();
        band.setId(123L);

        // act
        ValidationException ex = assertThrows(ValidationException.class, () -> {this.bandService.create(band);});

        // assert
        assertEquals("Can't create band with specific id", ex.getMessage());
    }

    @Test
    void testSetNameWorksCorrectly() {
        // arrange
        BandDao band = new BandDao();

        // act
        band.setName("  This is a  name   ");

        // assert
        assertEquals("This is a name", band.getName());
    }

    @Test
    void testSetNameToNullWorksCorrectly() {
        // arrange
        BandDao band = new BandDao();

        // act
        band.setName(null);

        // assert
        assertNull(band.getName());
    }

    @Test
    void testSetNameToEmptyStringWorksCorrectly() {
        // arrange
        BandDao band = new BandDao();

        // act
        band.setName("  ");

        // assert
        assertEquals("", band.getName());
    }

    @Test
    void testGetSectionTypeWorksSuccessfully() {
        // arrange
        Optional<SectionDao> firstSection = this.sectionService.fetchBySlug("first");

        BandDao band = new BandDao();
        band.setSection(firstSection.get());
        band.setStatus(BandStatus.COMPETING);

        // assert
        assertEquals("section.first", band.getSectionType());
    }

    @Test
    void testGetSectionTypeWhereNoSectionWorksSuccessfully() {
        // arrange
        BandDao band = new BandDao();
        band.setSection(null);
        band.setStatus(BandStatus.COMPETING);

        // assert
        assertEquals("status.competing", band.getSectionType());
    }

    @Test
    void testGetSectionTypeWhereYouthBandCompetingWorksSuccessfully() {
        // arrange
        Optional<SectionDao> youthSection = this.sectionService.fetchBySlug("youth");

        BandDao band = new BandDao();
        band.setSection(youthSection.get());
        band.setStatus(BandStatus.COMPETING);

        // assert
        assertEquals("status.youth", band.getSectionType());
    }

    @Test
    void testGetSectionTypeWhereYouthBandYouthSectionWorksSuccessfully() {
        // arrange
        Optional<SectionDao> youthSection = this.sectionService.fetchBySlug("youth");

        BandDao band = new BandDao();
        band.setSection(youthSection.get());
        band.setStatus(BandStatus.YOUTH);

        // assert
        assertEquals("status.youth", band.getSectionType());
    }
}

