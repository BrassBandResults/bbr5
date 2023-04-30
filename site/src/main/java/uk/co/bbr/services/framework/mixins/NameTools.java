package uk.co.bbr.services.framework.mixins;

public interface NameTools {

    default String simplifyCommon(String name) {
        return name.replaceAll(" +", " ").trim();
    }

    default String simplifyBandName(String name) {
        return simplifyCommon(name);
    }

    default String simplifyRegionName(String name) {
        return simplifyCommon(name);
    }

    default String simplifySectionName(String name) {
        return simplifyCommon(name);
    }

    default String simplifyContestName(String name) {
        return simplifyCommon(name);
    }

    default String simplifyPieceName(String name) {
        return simplifyCommon(name);
    }

    default String simplifyVenueName(String name) {
        return simplifyCommon(name);
    }

    default String simplifySurname(String name) {
        return simplifyCommon(name);
    }

    default String simplifyFirstName(String name) {
        // normalise to one space
        String singleSpaces = simplifyCommon(name + " ");

        // add a dot after any single initials
        String singleInitialNotAtEnd = singleSpaces.replaceAll("\\s([A-Za-z])\\s", " $1. ");

        // do it again to make sure we've picked them all up
        String endInitials = singleInitialNotAtEnd.replaceAll("\\s([A-Za-z])\\s", " $1. ");

        return endInitials.trim();
    }

    default String simplifyPersonFullName(String name) {
        return simplifyFirstName(name);
    }
}
