package uk.co.bbr.services.framework.mixins;

public interface NameTools {

    default String _simplifyCommon(String name) {
        return name.replaceAll(" +", " ")
                                .replaceAll("^St ", "St. ")
                                .replace(" St ", " St. ").trim();
    }

    default String _simplifyInitials(String name) {
        // add a dot after any single initials
        String singleInitialNotAtEnd = name.replaceAll("\\s([A-Z])\\s", " $1. ");

        // do it again to make sure we've picked them all up
        singleInitialNotAtEnd = singleInitialNotAtEnd.replaceAll("\\s([A-Z])\\s", " $1. ");

        // do same for first character in string if it's a single initial
        String endInitials = singleInitialNotAtEnd.replaceAll("^([A-Z])\\s", "$1. ");

        return endInitials.trim();
    }

    default String simplifyBandName(String name) {
        return _simplifyCommon(name);
    }

    default String simplifyRegionName(String name) {
        return _simplifyCommon(name);
    }

    default String simplifySectionName(String name) {
        return _simplifyCommon(name);
    }

    default String simplifyContestName(String name) {
        return _simplifyCommon(name);
    }

    default String simplifyPieceName(String name) {
        return _simplifyCommon(name);
    }

    default String simplifyVenueName(String name) {
        return _simplifyCommon(name);
    }

    default String simplifySurname(String name) {
        return _simplifyCommon(name);
    }

    default String simplifyFirstName(String name) {
        return _simplifyInitials(_simplifyCommon(name));
    }

    default String simplifyPersonFullName(String name) {
        return _simplifyInitials(_simplifyCommon(name));
    }
}
