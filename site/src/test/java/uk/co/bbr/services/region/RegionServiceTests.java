package uk.co.bbr.services.region;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.region.dao.RegionDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=region-tests-h2", "spring.datasource.url=jdbc:h2:mem:region-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
public class RegionServiceTests {

    @Autowired
    private RegionService regionService;

    @Test
    void testCreateRegion() {
        // arrange
        RegionDao newRegion = new RegionDao();
        newRegion.setName("Test Region");
        newRegion.setSlug("test-region");
        newRegion.setCountryCode("TEST");
        newRegion.setLatitude("0");
        newRegion.setLongitude("1");
        newRegion.setDefaultMapZoom(5);
        newRegion.setCreatedBy(1);
        newRegion.setUpdatedBy(2);

        // act
        RegionDao createdRegion = this.regionService.createRegion(newRegion);

        // assert
        assertEquals("Test Region", createdRegion.getName());
        assertEquals("test-region", createdRegion.getSlug());
        assertEquals("TEST", createdRegion.getCountryCode());
        assertEquals("0", createdRegion.getLatitude());
        assertEquals("1", createdRegion.getLongitude());
        assertEquals(5, createdRegion.getDefaultMapZoom());
        assertEquals(1, createdRegion.getCreatedBy());
        assertEquals(2, createdRegion.getUpdatedBy());

        this.regionService.deleteRegion(createdRegion);
    }

    @Test
    void testListRegions() {
        // arrange
        RegionDao newRegion1 = new RegionDao();
        newRegion1.setName("Test Region");
        newRegion1.setSlug("test-region");
        newRegion1.setCountryCode("TEST");
        newRegion1.setLatitude("0");
        newRegion1.setLongitude("1");
        newRegion1.setDefaultMapZoom(5);
        newRegion1.setCreatedBy(1);
        newRegion1.setUpdatedBy(2);
        RegionDao createdRegion1 = this.regionService.createRegion(newRegion1);

        RegionDao newRegion2 = new RegionDao();
        newRegion2.setName("Test Region 2");
        newRegion2.setSlug("test-region-2");
        newRegion2.setCountryCode("UK");
        newRegion2.setLatitude("5");
        newRegion2.setLongitude("6");
        newRegion2.setDefaultMapZoom(7);
        newRegion2.setCreatedBy(3);
        newRegion2.setUpdatedBy(4);
        RegionDao createdRegion2 = this.regionService.createRegion(newRegion2);

        // act
        List<RegionDao> allRegions = this.regionService.fetchAllRegions();

        // assert
        assertEquals(2, allRegions.size());

        assertEquals("Test Region", allRegions.get(0).getName());
        assertEquals("test-region", allRegions.get(0).getSlug());
        assertEquals("TEST", allRegions.get(0).getCountryCode());
        assertEquals("0", allRegions.get(0).getLatitude());
        assertEquals("1", allRegions.get(0).getLongitude());
        assertEquals(5, allRegions.get(0).getDefaultMapZoom());
        assertEquals(1, allRegions.get(0).getCreatedBy());
        assertEquals(2, allRegions.get(0).getUpdatedBy());

        assertEquals("Test Region 2", allRegions.get(1).getName());
        assertEquals("test-region-2", allRegions.get(1).getSlug());
        assertEquals("UK", allRegions.get(1).getCountryCode());
        assertEquals("5", allRegions.get(1).getLatitude());
        assertEquals("6", allRegions.get(1).getLongitude());
        assertEquals(7, allRegions.get(1).getDefaultMapZoom());
        assertEquals(3, allRegions.get(1).getCreatedBy());
        assertEquals(4, allRegions.get(1).getUpdatedBy());

        this.regionService.deleteRegion(createdRegion1);
        this.regionService.deleteRegion(createdRegion2);
        allRegions = this.regionService.fetchAllRegions();
        assertEquals(0, allRegions.size());
    }
}
