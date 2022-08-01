package uk.co.bbr.services.bands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.band.BandService;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.band.types.BandStatus;
import uk.co.bbr.services.region.RegionService;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.section.SectionService;
import uk.co.bbr.services.section.dao.SectionDao;
import uk.co.bbr.web.LoginMixin;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=create-band-tests-h2", "spring.datasource.url=jdbc:h2:mem:create-band-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
@WithMockUser(username="member_user", roles= { "BBR_MEMBER" })
class CreateBandServiceTests implements LoginMixin {

    @Autowired private BandService bandService;
    @Autowired private RegionService regionService;
    @Autowired private SectionService sectionService;

    @Test
    void testCreateBandWorksSuccessfullyWithJustName() {
        // act
        BandDao createdBand = this.bandService.create("Black Dyke Band");

        // assert
        assertNotNull(createdBand.getId());
        assertNotNull(createdBand.getCreated());
        assertEquals(1, createdBand.getCreatedBy());
        assertNotNull(createdBand.getUpdated());
        assertEquals(1, createdBand.getUpdatedBy());
        assertEquals("Black Dyke Band", createdBand.getName());
        assertEquals("black-dyke-band", createdBand.getSlug());
        assertEquals("Unknown", createdBand.getRegion().getName());
    }

    @Test
    void testCreateBandWithMultipleSpacesInNameCompressesSpaces() {
        BandDao createdBand = this.bandService.create("  Roth well  Temperance   Band   ");
        assertEquals("Roth well Temperance Band", createdBand.getName());
        assertEquals("roth-well-temperance-band", createdBand.getSlug());
    }

    @Test
    void testCreateBandWithRegionWorkSuccessfully() {
        // arrange
        RegionDao northWestRegion = this.regionService.findBySlug("north-west");

        // act
        BandDao band = this.bandService.create("Foden's", northWestRegion);

        // assert
        assertEquals("Foden's", band.getName());
        assertEquals("foden-s", band.getSlug());
        assertEquals("North West", band.getRegion().getName());
    }

    @Test
    void testCreateBandFromObjectWorksSuccessfully() {
        // arrange
        RegionDao yorkshireRegion = this.regionService.findBySlug("yorkshire");
        SectionDao championshipSection = this.sectionService.fetchBySlug("championship");

        BandDao newBand = new BandDao();
        newBand.setName("  Yorkshire Building Society Band  Of Yorkshire formerly Hammonds Sauce Works Band ");
        newBand.setRegion(yorkshireRegion);
        newBand.setLatitude(" 53.792614944591655 ");
        newBand.setLongitude(" -1.7510766401965498 ");
        newBand.setStartDate(LocalDate.of(1900, 1, 1));
        newBand.setEndDate(LocalDate.of(2000, 12, 31));
        newBand.setMapperId(1L);
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
        assertEquals(1L, createdBand.getMapperId());
        assertEquals("Championship", createdBand.getSection().getName());
        assertEquals("Test Notes", createdBand.getNotes());
        assertEquals(BandStatus.EXTINCT, createdBand.getStatus());
        assertEquals("Twitter", createdBand.getTwitterName());
        assertEquals("http://ybsband.org.uk", createdBand.getWebsite());
    }
}

