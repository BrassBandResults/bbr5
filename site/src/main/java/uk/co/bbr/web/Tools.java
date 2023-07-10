package uk.co.bbr.web;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class Tools {

    public static String format(String inputString) {
        if (inputString == null || inputString.trim().length() == 0) {
            return "";
        }

        String outputString = inputString.trim();
        outputString = outputString.replace("&", "&amp;");
        outputString = outputString.replace("<", "&lt;");
        outputString = outputString.replace(">", "&gt;");
        outputString = outputString.replace("\n", "<br/>");

        return outputString;
    }

    public static LocalDate parseEventDate(String contestEventDate) {
        String[] dateSplit = contestEventDate.split("-");
        return LocalDate.of(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));
    }
}
