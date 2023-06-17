package uk.co.bbr.web;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HtmlTools {

    public String format(String inputString) {
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
}
