package uk.co.bbr.services.groups.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.bands.dao.BandDao;
import uk.co.bbr.services.regions.dao.RegionDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class WhitFridayOverallBandResultDto {
    private final String bandName;
    private final String bandSlug;
    private final String bandRegionName;
    private final String bandRegionSlug;
    private final String bandRegionCode;

    private List<Integer> results = new ArrayList<>();

    @Setter
    private int position;

    public BandDao getBand() {
        BandDao returnBand = new BandDao();
        returnBand.setName(this.bandName);
        returnBand.setSlug(this.bandSlug);
        returnBand.setRegion(new RegionDao());
        returnBand.getRegion().setName(this.bandRegionName);
        returnBand.getRegion().setSlug(this.bandRegionSlug);
        returnBand.getRegion().setCountryCode(this.bandRegionCode);

        return returnBand;
    }

    public int getTotalResults() {
        int returnTotal = 0;
        int count = 0;
        for (Integer eachResult : this.results) {
            returnTotal += eachResult;
            count++;
            if (count >= 6){
                // only add top six results
                break;
            }
        }
        return returnTotal;
    }

    public int getMedianResult() {
        int value1 = this.results.get(2);
        int value2 = this.results.get(3);
        return (value1 + value2) / 2;
    }

    public void addResult(int position) {
        this.results.add(position);
    }

    public void sortResults() {
        this.results = this.results.stream().sorted().collect(Collectors.toList());
    }
}
