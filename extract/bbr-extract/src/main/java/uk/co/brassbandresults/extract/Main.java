package uk.co.brassbandresults.extract;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, IOException {
        System.out.println("Extracting Brass Band Results Data");

        String DATABASE_USERNAME = args[0];
        String DATABASE_PASSWORD = args[1];
        String DATABASE_HOST = args[2];
        String DATABASE_NAME = args[3];

        String connectionUrl = "jdbc:sqlserver://"+DATABASE_HOST+";databaseName="+DATABASE_NAME+";encrypt=true;trustServerCertificate=true;user="+DATABASE_USERNAME+";password="+DATABASE_PASSWORD;

        DataFetcher dataFetcher = new DataFetcher(connectionUrl);
        DataWriter dataWriter = new DataWriter(dataFetcher);
        dataWriter.writeSince(2023, 1, 1);
    }
}
