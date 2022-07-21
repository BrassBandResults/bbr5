package uk.co.bbr.services.region;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.region.dao.RegionDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=region-tests-h2", "spring.datasource.url=jdbc:h2:mem:region-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class RegionServiceTests {

    @Autowired
    private RegionService regionService;

    @Test
    void testCreateRegionWorksSuccessfully() {
        // arrange
        RegionDao newRegion = new RegionDao();
        newRegion.setName("Test Region");
        newRegion.setSlug("test-region");
        newRegion.setCountryCode("TEST");
        newRegion.setLatitude("0");
        newRegion.setLongitude("1");
        newRegion.setDefaultMapZoom(5);
        newRegion.setCreatedBy(1);
        newRegion.setUpdatedBy(1);

        // act
        RegionDao createdRegion = this.regionService.create(newRegion);

        // assert
        assertEquals("Test Region", createdRegion.getName());
        assertEquals("test-region", createdRegion.getSlug());
        assertEquals("TEST", createdRegion.getCountryCode());
        assertEquals("0", createdRegion.getLatitude());
        assertEquals("1", createdRegion.getLongitude());
        assertEquals(5, createdRegion.getDefaultMapZoom());
        assertEquals(1, createdRegion.getCreatedBy());
        assertEquals(1, createdRegion.getUpdatedBy());

        this.regionService.delete(createdRegion);
    }

    @Test
    void testCreateRegionByNameWorksSuccessfully() {
        // act
        RegionDao createdRegion = this.regionService.create("Yorkshire Region");

        // assert
        assertEquals("Yorkshire Region", createdRegion.getName());
        assertEquals("yorkshire-region", createdRegion.getSlug());
        assertNull(createdRegion.getCountryCode());
        assertNull(createdRegion.getLatitude());
        assertNull(createdRegion.getLongitude());
        assertNull(createdRegion.getDefaultMapZoom());
        assertEquals(1, createdRegion.getCreatedBy());
        assertEquals(1, createdRegion.getUpdatedBy());
    }

    @Test
    void testListRegionsWorksSuccessfully() {
        // arrange
        RegionDao newRegion1 = new RegionDao();
        newRegion1.setName("Test Region");
        newRegion1.setSlug("test-region");
        newRegion1.setCountryCode("TEST");
        newRegion1.setLatitude("0");
        newRegion1.setLongitude("1");
        newRegion1.setDefaultMapZoom(5);
        newRegion1.setCreatedBy(1);
        newRegion1.setUpdatedBy(1);
        RegionDao createdRegion1 = this.regionService.create(newRegion1);

        RegionDao newRegion2 = new RegionDao();
        newRegion2.setName("Test Region 2");
        newRegion2.setSlug("test-region-2");
        newRegion2.setCountryCode("UK");
        newRegion2.setLatitude("5");
        newRegion2.setLongitude("6");
        newRegion2.setDefaultMapZoom(7);
        newRegion2.setCreatedBy(1);
        newRegion2.setUpdatedBy(1);
        RegionDao createdRegion2 = this.regionService.create(newRegion2);

        // act
        List<RegionDao> allRegions = this.regionService.fetchAll();

        // assert
        assertEquals(3, allRegions.size());

        assertEquals("Unknown", allRegions.get(0).getName());
        assertEquals("unknown", allRegions.get(0).getSlug());
        assertEquals("none", allRegions.get(0).getCountryCode());
        assertNull(allRegions.get(0).getLatitude());
        assertNull(allRegions.get(0).getLongitude());
        assertEquals(null, allRegions.get(0).getDefaultMapZoom());
        assertEquals(1, allRegions.get(0).getCreatedBy());
        assertEquals(1, allRegions.get(0).getUpdatedBy());

        assertEquals("Test Region", allRegions.get(1).getName());
        assertEquals("test-region", allRegions.get(1).getSlug());
        assertEquals("TEST", allRegions.get(1).getCountryCode());
        assertEquals("0", allRegions.get(1).getLatitude());
        assertEquals("1", allRegions.get(1).getLongitude());
        assertEquals(5, allRegions.get(1).getDefaultMapZoom());
        assertEquals(1, allRegions.get(1).getCreatedBy());
        assertEquals(1, allRegions.get(1).getUpdatedBy());

        assertEquals("Test Region 2", allRegions.get(2).getName());
        assertEquals("test-region-2", allRegions.get(2).getSlug());
        assertEquals("UK", allRegions.get(2).getCountryCode());
        assertEquals("5", allRegions.get(2).getLatitude());
        assertEquals("6", allRegions.get(2).getLongitude());
        assertEquals(7, allRegions.get(2).getDefaultMapZoom());
        assertEquals(1, allRegions.get(2).getCreatedBy());
        assertEquals(1, allRegions.get(2).getUpdatedBy());

        this.regionService.delete(createdRegion1);
        this.regionService.delete(createdRegion2);
        allRegions = this.regionService.fetchAll();
        assertEquals(1, allRegions.size());
    }
}
