package uk.co.bbr.services.events.dto;

import lombok.Getter;
import uk.co.bbr.services.events.dao.ContestResultDao;
import uk.co.bbr.services.events.types.ResultPositionType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public class ResultDetailsDto {
    private final List<ContestResultDao> bandNonWhitResults;
    private final List<ContestResultDao> bandWhitResults;
    private final List<ContestResultDao> bandAllResults;
    private final List<ContestResultDao> currentChampions;
    private final List<SpecialAwardDto> specialAwards;
    private final int nonWhitWinsCount;
    private final int nonWhitTopSixCount;
    private final int nonWhitUnplacedCount;
    private final int whitWinsCount;
    private final int whitTopSixCount;
    private final int whitUnplacedCount;

    public ResultDetailsDto(List<ContestResultDao> bandNonWhitResults, List<ContestResultDao> bandWhitResults, List<ContestResultDao> bandAllResults, List<ContestResultDao> currentChampions) {
        this.bandNonWhitResults = bandNonWhitResults;
        this.bandWhitResults = bandWhitResults;
        this.bandAllResults = bandAllResults;
        this.currentChampions = currentChampions;

        this.specialAwards = this.lookForSpecialAwards();

        int tNonWhitWinsCount = 0;
        int tNonWhitTopSixCount = 0;
        int tNonWhitUnplacedCount = 0;
        int tWhitWinsCount = 0;
        int tWhitTopSixCount = 0;
        int tWhitUnplacedCount = 0;

        for (ContestResultDao eachResult : bandNonWhitResults) {
            if (eachResult.getResultPositionType() == ResultPositionType.RESULT && eachResult.getPosition() == 1) {
                tNonWhitWinsCount++;
                continue;
            }
            if (eachResult.getResultPositionType() == ResultPositionType.RESULT && eachResult.getPosition() >= 2 && eachResult.getPosition() <= 6 ) {
                tNonWhitTopSixCount++;
                continue;
            }
            tNonWhitUnplacedCount++;
        }

        for (ContestResultDao eachResult : bandWhitResults) {
            if (eachResult.getResultPositionType() == ResultPositionType.RESULT && eachResult.getPosition() == 1) {
                tWhitWinsCount++;
                continue;
            }
            if (eachResult.getResultPositionType() == ResultPositionType.RESULT && eachResult.getPosition() >= 2 && eachResult.getPosition() <= 6 ) {
                tWhitTopSixCount++;
                continue;
            }
            tWhitUnplacedCount++;
        }

        nonWhitWinsCount = tNonWhitWinsCount;
        nonWhitTopSixCount = tNonWhitTopSixCount;
        nonWhitUnplacedCount = tNonWhitUnplacedCount;
        whitWinsCount = tWhitWinsCount;
        whitTopSixCount = tWhitTopSixCount;
        whitUnplacedCount = tWhitUnplacedCount;
    }

    private List<SpecialAwardDto> lookForSpecialAwards() {
        List<SpecialAwardDto> returnList = new ArrayList<>();

        List<ContestResultDao> filteredWinningResults = this.bandNonWhitResults.stream()
            .filter(r -> r.getResultPositionType().equals(ResultPositionType.RESULT))
            .filter(r -> r.getPosition() == 1)
            .filter(r -> r.getContestEvent().getContest().getSlug().equals("national-finals-championship-section")
              || r.getContestEvent().getContest().getSlug().equals("british-open")
              || r.getContestEvent().getContest().getSlug().equals("european-championships"))
            .sorted(Comparator.comparing(o -> o.getContestEvent().getEventDate()))
            .toList();

        Map<Integer, List<ContestResultDao>> yearWins = new HashMap<>();
        for (ContestResultDao eachResult : filteredWinningResults) {
            List<ContestResultDao> winsForThisYear = yearWins.computeIfAbsent(eachResult.getContestEvent().getEventDate().getYear(), k -> new ArrayList<>());
            winsForThisYear.add(eachResult);
        }

        for (Integer year : yearWins.keySet()) {
            List<ContestResultDao> winsForThisYear = yearWins.get(year);
            if (winsForThisYear.size() <= 1) {
                continue;
            }

            Optional<ContestResultDao> foundNationalsWin = winsForThisYear.stream().filter(r-> r.getContestEvent().getContest().getSlug().equals("national-finals-championship-section")).findAny();
            Optional<ContestResultDao> foundBritishOpenWin = winsForThisYear.stream().filter(r-> r.getContestEvent().getContest().getSlug().equals("british-open")).findAny();
            Optional<ContestResultDao> foundEuropeansWin = winsForThisYear.stream().filter(r-> r.getContestEvent().getContest().getSlug().equals("european-championships")).findAny();

            if (foundNationalsWin.isPresent() && foundBritishOpenWin.isPresent() && foundEuropeansWin.isPresent()) {
                returnList.add(new SpecialAwardDto(year.toString(), "page.band.special-award.grand-slam"));
                continue;
            }

            if (foundNationalsWin.isPresent() && foundBritishOpenWin.isPresent()) {
                returnList.add(new SpecialAwardDto(year.toString(), "page.band.special-award.double"));
            }
        }

        return returnList.stream().sorted(Comparator.comparing(SpecialAwardDto::getYear)).toList();
    }
}
