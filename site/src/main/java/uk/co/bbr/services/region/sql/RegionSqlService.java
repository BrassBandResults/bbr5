package uk.co.bbr.services.region.sql;

import uk.co.bbr.services.region.dto.RegionPageDto;

public interface RegionSqlService {
    RegionPageDto findBySlugForPage(String regionSlug);
}
