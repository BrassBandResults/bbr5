package uk.co.bbr.services.regions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = {  "spring.config.location=classpath:test-application.yml",
        "spring.datasource.url=jdbc:h2:mem:regions-region-services-tests-admin-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
class RegionServiceTests {

    @Autowired private RegionService regionService;

    @Test
    void testUnknownRegionReturnsSuccessfully() {
        RegionDao unknownRegion = this.regionService.fetchUnknownRegion();
        assertEquals("Unknown", unknownRegion.getName());
        assertEquals("unknown", unknownRegion.getSlug());
    }

    @Test
    void testListRegionsWorksSuccessfully() {
        // act
        List<RegionDao> allRegions = this.regionService.findAll();

        // assert
        assertEquals(65, allRegions.size());
    }
}
