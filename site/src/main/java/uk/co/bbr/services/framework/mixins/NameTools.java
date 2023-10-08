package uk.co.bbr.services.framework.mixins;

public interface NameTools {

    default String replaceCommon(String name) {
        return name.replaceAll(" +", " ")
                   .replaceAll("^St ", "St. ")
                   .replace(" St ", " St. ")
                   .replaceAll("\"", "")
                   .strip();
    }

    default String makeSureSpaceAfterDot(String name) {
        boolean foundDot = false;
        StringBuilder returnValue = new StringBuilder();
        for (char eachChar : name.toCharArray()) {
            if (foundDot) {
                if (eachChar != ' ') {
                    returnValue.append(" ");
                }
                foundDot = false;
            }
            if (eachChar == '.') {
                foundDot = true;
            }
            returnValue.append(eachChar);
        }
        return returnValue.toString();
    }

    default String addDotAfterInitials(String name) {
        // add a dot after any single initials
        String singleInitialNotAtEnd = name.replaceAll("\\s([A-Z])\\s", " $1. ");

        // do it again to make sure we've picked them all up
        singleInitialNotAtEnd = singleInitialNotAtEnd.replaceAll("\\s([A-Z])\\s", " $1. ");

        // do same for first character in string if it's a single initial
        String endInitials = singleInitialNotAtEnd.replaceAll("^([A-Z])\\s", "$1. ");

        return endInitials.strip();
    }

    default String simplifyBandName(String name) {
        return replaceCommon(name);
    }

    default String simplifyRegionName(String name) {
        return replaceCommon(name);
    }

    default String simplifySectionName(String name) {
        return replaceCommon(name);
    }

    default String simplifyContestName(String name) {
        return replaceCommon(name);
    }

    default String simplifyPieceName(String name) {
        return replaceCommon(name);
    }

    default String simplifyVenueName(String name) {
        return replaceCommon(name);
    }

    default String simplifySurname(String name) {
        return replaceCommon(name);
    }

    default String simplifyFirstName(String name) {
        return addDotAfterInitials(makeSureSpaceAfterDot(replaceCommon(name)));
    }

    default String simplifyPersonFullName(String name) {
        return addDotAfterInitials(makeSureSpaceAfterDot(replaceCommon(name)));
    }
}
