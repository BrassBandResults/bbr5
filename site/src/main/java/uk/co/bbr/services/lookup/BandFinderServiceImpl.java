package uk.co.bbr.services.lookup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.bands.repo.BandPreviousNameRepository;
import uk.co.bbr.services.bands.repo.BandRepository;
import uk.co.bbr.services.framework.mixins.SlugTools;
import uk.co.bbr.services.lookup.sql.FinderSql;
import uk.co.bbr.services.lookup.sql.dto.FinderSqlDto;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BandFinderServiceImpl implements BandFinderService, SlugTools {
    private final BandRepository bandRepository;
    private final BandPreviousNameRepository bandPreviousNameRepository;
    private final EntityManager entityManager;


    @Override
    public BandDao findMatchByName(String searchBandName, LocalDate dateContext) {
        if (searchBandName == null || searchBandName.length() < 3) {
            return null;
        }

        String bandNameUpper = searchBandName.toUpperCase().strip();
        String bandNameUpperLessBand = null;
        if (bandNameUpper.endsWith("BAND")) {
            bandNameUpperLessBand = bandNameUpper.substring(0, bandNameUpper.length() - "BAND".length()).strip();
        }

        List<FinderSqlDto> bandMatches = FinderSql.bandFindExactNameMatch(this.entityManager, bandNameUpper);
        bandMatches.addAll(FinderSql.bandAliasFindExactNameMatch(this.entityManager, bandNameUpper));
        bandMatches.addAll(FinderSql.bandFindExactNameMatch(this.entityManager, bandNameUpper + " BAND"));
        bandMatches.addAll(FinderSql.bandAliasFindExactNameMatch(this.entityManager, bandNameUpper + " BAND"));
        bandMatches.addAll(FinderSql.bandFindContainsNameMatch(this.entityManager, bandNameUpper + " "));
        bandMatches.addAll(FinderSql.bandAliasFindContainsNameMatch(this.entityManager, bandNameUpper + " "));
        bandMatches.addAll(FinderSql.bandFindContainsNameMatch(this.entityManager, bandNameUpper));
        bandMatches.addAll(FinderSql.bandAliasFindContainsNameMatch(this.entityManager, bandNameUpper));

        if (bandNameUpperLessBand != null) {
            bandMatches.addAll(FinderSql.bandFindContainsNameMatch(this.entityManager, bandNameUpperLessBand));
            bandMatches.addAll(FinderSql.bandAliasFindContainsNameMatch(this.entityManager, bandNameUpperLessBand));
        }

        List<FinderSqlDto> returnList = new ArrayList<>();

        // remove any matches that fall outside the date context
        for (FinderSqlDto match : bandMatches) {
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

        BandDao returnBand = new BandDao();
        returnBand.setName(returnList.get(0).getName());
        returnBand.setSlug(returnList.get(0).getSlug());
        return returnBand;
    }
}
