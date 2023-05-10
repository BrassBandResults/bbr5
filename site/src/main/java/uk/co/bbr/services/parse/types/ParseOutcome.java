package uk.co.bbr.services.parse.types;

public enum ParseOutcome {
    RED_FAILED_PARSE, // Unable to parse
    AMBER_PARSE_SUCCEEDED, // Parsed but couldn't find matches
    GREEN_MATCHES_FOUND_IN_DATABASE, // Parsed and found matches
}
