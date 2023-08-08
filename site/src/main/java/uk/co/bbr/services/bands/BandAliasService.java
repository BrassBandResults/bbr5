package uk.co.bbr.services.bands;

import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dao.BandAliasDao;

import java.util.List;
import java.util.Optional;

public interface BandAliasService {
    BandAliasDao createAlias(BandDao band, BandAliasDao previousName);
    BandAliasDao createAlias(BandDao band, String name);
    Optional<BandAliasDao> aliasExists(BandDao band, String aliasName);

    List<BandAliasDao> findVisibleAliases(BandDao band);
    List<BandAliasDao> findAllAliases(BandDao band);

    void showAlias(BandDao band, Long aliasId);

    void hideAlias(BandDao band, Long aliasId);

    void deleteAlias(BandDao band, Long aliasId);

    Optional<BandAliasDao> fetchAliasByBandAndId(BandDao band, Long aliasId);

    void updateAlias(BandDao band, BandAliasDao previousName);
}
