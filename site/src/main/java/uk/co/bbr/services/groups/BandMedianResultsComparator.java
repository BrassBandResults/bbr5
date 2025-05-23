package uk.co.bbr.services.groups;

import uk.co.bbr.services.groups.dto.WhitFridayOverallBandResultDto;

import java.util.Comparator;

public class BandMedianResultsComparator implements Comparator<WhitFridayOverallBandResultDto> {

        public int compare(WhitFridayOverallBandResultDto first, WhitFridayOverallBandResultDto second)
        {
            if (first.getMedianResult() < second.getMedianResult()) {
                return -1;
            }
            else if (first.getMedianResult() > second.getMedianResult()) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }
