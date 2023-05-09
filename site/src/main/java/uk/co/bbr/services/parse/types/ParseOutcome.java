package uk.co.bbr.services.parse.types;

public enum ParseOutcome {
    RED, // Unable to parse
    AMBER, // Parsed but couldn't find matches
    GREEN, // Parsed and found matches
}
