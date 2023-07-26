package uk.co.bbr.map;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.lookup.sql.dto.LookupSqlDto;

import java.util.List;

public interface LocationService {

    void updateBandLocation(BandDao band);
}

