package uk.co.bbr.services.bands;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.dto.BandCompareDto;
import uk.co.bbr.services.bands.dto.BandListBandDto;
import uk.co.bbr.services.bands.dto.BandListDto;
import uk.co.bbr.services.bands.repo.BandPreviousNameRepository;
import uk.co.bbr.services.bands.repo.BandRepository;
import uk.co.bbr.services.bands.sql.BandCompareSql;
import uk.co.bbr.services.bands.sql.BandSql;
import uk.co.bbr.services.bands.sql.dto.BandWinnersSqlDto;
import uk.co.bbr.services.bands.sql.dto.CompareBandsSqlDto;
import uk.co.bbr.services.bands.types.BandStatus;
import uk.co.bbr.services.framework.ValidationException;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.regions.RegionService;
import uk.co.bbr.services.regions.dao.RegionDao;
import uk.co.bbr.services.security.SecurityService;
import uk.co.bbr.web.security.annotations.IsBbrAdmin;
import uk.co.bbr.web.security.annotations.IsBbrMember;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BandFinderServiceImpl implements BandFinderService, SlugTools {
    private final BandRepository bandRepository;
    private final BandPreviousNameRepository bandPreviousNameRepository;
    private final EntityManager entityManager;


    @Override
    public BandDao findMatchByName(String searchBandName, LocalDate dateContext) {
        String bandName = searchBandName.toUpperCase().trim();
        String bandNameLessBand = null;
        if (bandName.toLowerCase().endsWith("band")) {
            bandNameLessBand = bandName.substring(0, bandName.length() - "band".length()).trim();
        }

        List<BandDao> bandMatches = this.bandRepository.findExactNameMatch(bandName);

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandPreviousNameRepository.findAliasExactNameMatch(bandName);
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandRepository.findExactNameMatch(bandName + " Band");
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandPreviousNameRepository.findAliasExactNameMatch(bandName + " Band");
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandRepository.findContainsNameMatch("%" + bandName + "% ");
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandPreviousNameRepository.findContainsNameMatch("%" + bandName + "% ");
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandRepository.findContainsNameMatch("%" + bandName + "%") ;
        }

        if (bandMatches.isEmpty()) {
            bandMatches = this.bandPreviousNameRepository.findContainsNameMatch("%" + bandName + "%");
        }

        if (bandNameLessBand != null) {
            if (bandMatches.isEmpty()) {
                bandMatches = this.bandRepository.findContainsNameMatch("%" + bandNameLessBand + "%");
            }

            if (bandMatches.isEmpty()) {
                bandMatches = this.bandPreviousNameRepository.findContainsNameMatch("%" + bandNameLessBand + "%");
            }
        }

        List<BandDao> returnList = new ArrayList<>();

        // remove any matches that fall outside the date context
        for (BandDao match : bandMatches) {
            if (match.getStartDate() != null) {
                if (match.getStartDate().isAfter(dateContext)) {
                    continue;
                }
            }

            if (match.getEndDate() != null) {
                if (match.getEndDate().isBefore(dateContext)) {
                    continue;
                }
            }

            returnList.add(match);
        }

        if (returnList.isEmpty()) {
            return null;
        }

        return returnList.get(0);
    }
}
