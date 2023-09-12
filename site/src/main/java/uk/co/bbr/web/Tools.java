package uk.co.bbr.web;

import lombok.experimental.UtilityClass;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.time.LocalDate;

@UtilityClass
public class Tools {

    public static String format(String inputString) {
        if (inputString == null || inputString.strip().length() == 0) {
            return "";
        }

        String outputString = inputString.strip();
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

    public static String markdownToHTML(String markdown) {
        if (markdown == null || markdown.trim().length() == 0) {
            return "";
        }
        Parser parser = Parser.builder().build();

        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).softbreak("<br/>").build();

        return renderer.render(document);
    }
}
