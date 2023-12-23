package uk.co.brassbandresults.extract.json;

import lombok.Getter;
import uk.co.brassbandresults.extract.data.AdjudicatorData;

@Getter
public class AdjudicatorJson {
    private final String slug;
    private final String firstNames;
    private final String surname;

    public AdjudicatorJson(AdjudicatorData eachAdjudicator) {
        this.slug = eachAdjudicator.getSlug();
        this.firstNames = eachAdjudicator.getFirstNames();
        this.surname = eachAdjudicator.getSurname();
    }
}
