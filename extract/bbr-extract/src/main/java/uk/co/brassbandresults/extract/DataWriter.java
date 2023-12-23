package uk.co.brassbandresults.extract;

import uk.co.brassbandresults.extract.data.AdjudicatorData;
import uk.co.brassbandresults.extract.data.ContestEventData;
import uk.co.brassbandresults.extract.data.ContestResultData;
import uk.co.brassbandresults.extract.data.PieceData;
import uk.co.brassbandresults.extract.json.ContestEventJson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DataWriter {

    private static final String BASE_OUTPUT_PATH = "/Users/timsawyer/web/bbr-data-extract/events";
    private final boolean slow = true;

    private final DataFetcher dataFetcher;

    public DataWriter(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    public void writeSince(int year, int month, int day) throws SQLException, IOException {
        this.dataFetcher.connect();

        List<ContestEventData> contestEventData = this.dataFetcher.fetchContestsSince(year, month, day);
        for (ContestEventData eachEvent : contestEventData) {
            ContestEventJson contestEventJson = new ContestEventJson(eachEvent);
            String filePath = BASE_OUTPUT_PATH + "/" + contestEventJson.eventYear() + "/" + contestEventJson.eventMonth() + "/" + contestEventJson.getContestSlug() + ".json";

            List<ContestResultData> resultData = this.dataFetcher.fetchResultsFor(eachEvent);
            List<AdjudicatorData> adjudicators = this.dataFetcher.fetchAdjudicatorsFor(eachEvent);
            List<PieceData> setTests = this.dataFetcher.fetchSetTestsFor(eachEvent);
            List<PieceData> ownChoice = this.dataFetcher.fetchOwnChoiceFor(eachEvent);


            contestEventJson.addResults(resultData);
            contestEventJson.addAdjudicators(adjudicators);
            contestEventJson.addSetTests(setTests);
            contestEventJson.addOwnChoice(ownChoice);

            contestEventJson.writeData(filePath);

            if (slow) {
                try {
                    Thread.sleep(100);
                    System.out.print(".");
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }

        this.dataFetcher.disconnect();
    }
}
