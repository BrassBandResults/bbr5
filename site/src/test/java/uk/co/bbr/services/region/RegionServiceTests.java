package uk.co.bbr.services.region;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bbr.services.region.dao.RegionDao;
import uk.co.bbr.services.region.dto.RegionListDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = { "spring.config.name=region-tests-h2", "spring.datasource.url=jdbc:h2:mem:region-tests-h2;DB_CLOSE_DELAY=-1;MODE=MSSQLServer;DATABASE_TO_LOWER=TRUE"})
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
        List<RegionDao> allRegions = this.regionService.fetchAll();

        // assert
        assertEquals(63, allRegions.size());
    }

    @Test
    void testFetchRegionsForListPageWorksSuccessfully() {
        // act
        List<RegionListDto> allRegions = this.regionService.fetchRegionsForListPage();

        // assert
        assertEquals(63, allRegions.size());
    }
}
