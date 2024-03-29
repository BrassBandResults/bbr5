package uk.co.bbr.services.framework;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Record not found")
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    private static NotFoundException notFoundBySlug(String type, String slug) {
        return new NotFoundException(type + " with slug " + slug + " not found");
    }

    public static NotFoundException bandNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Band", slug);
    }

    public static NotFoundException pieceNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Piece", slug);
    }

    public static NotFoundException contestNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Contest", slug);
    }

    public static NotFoundException personNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Person", slug);
    }

    public static NotFoundException groupNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Group", slug);
    }

    public static NotFoundException venueNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Venue", slug);
    }

    public static NotFoundException tagNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Tag", slug);
    }

    public static NotFoundException regionNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Region", slug);
    }

    public static Exception sectionNotFoundBySlug(String slug) {
        return NotFoundException.notFoundBySlug("Section", slug);
    }

    public static NotFoundException userNotFoundByUsercode(String usercode) {
        return new NotFoundException("User with usercode " + usercode + " not found");
    }

    public static NotFoundException eventNotFound(String contestSlug, String contestEventDate) {
        return new NotFoundException("Event with slug " + contestSlug + " and date " + contestEventDate + " not found");
    }

    public static NotFoundException bandAliasNotFoundByIds(String bandSlug, long aliasId) {
        return new NotFoundException("Alias with id " + aliasId + " not found for band with slug " + bandSlug);
    }

    public static NotFoundException lookupNeedsThreeCharacters() {
        return new NotFoundException("Lookup needs to have three or more characters");
    }

    public static NotFoundException lookupTypeNotFound(String type) {
        return new NotFoundException("Lookup type " + type + " not found");
    }

    public static NotFoundException relationshipNotFoundById(Long relationshipId) {
        return new NotFoundException("Band Relationship with id " + relationshipId + " not found");
    }

    public static NotFoundException bandRelationshipTypeNotFoundById(String relationshipTypeId) {
        return new NotFoundException("Band Relationship type with id " + relationshipTypeId + " not found");
    }

    public static NotFoundException personRelationshipTypeNotFoundById(String relationshipTypeId) {
        return new NotFoundException("Person Relationship type with id " + relationshipTypeId + " not found");
    }

    public static NotFoundException feedbackNotFoundById(long feedbackId) {
        return new NotFoundException("Feedback with id " + feedbackId + " not found");
    }

    public static NotFoundException feedbackUpdateNotFound(String type) {
        return new NotFoundException("Feedback update type " + type + " not recognised");
    }

    public static NotFoundException userNotFoundByActivationKey() {
        return new NotFoundException("Couldn't find a user to activate with that key");
    }

    public static NotFoundException userNotFoundByResetPasswordKey() {
        return new NotFoundException("Couldn't find a user to reset password for with that key");
    }

    public static NotFoundException userNotFoundByRandomString() {
        return new NotFoundException("Couldn't find a user to opt out for with that key");
    }

    public static NotFoundException contestTypeNotFoundForId(Long contestTypeId) {
        return new NotFoundException("Contest type with id " + contestTypeId + " not found");
    }

    public static NotFoundException resultNotFoundById(Long resultId) {
        return new NotFoundException("Result with id " + resultId + " not found");
    }

    public static NotFoundException resultNotOnCorrectContest(String contestSlug, String contestEventDate) {
        return new NotFoundException("Result not found on event with slug " + contestSlug + " and date " + contestEventDate);
    }

    public static NotFoundException yearOutsideRange(int year) {
        return new NotFoundException("Year " + year + " is outside the valid range for the calendar");
    }

    public static NotFoundException setTestPieceNotFoundById() {
        return new NotFoundException("Contest Set Test Piece not found by id");
    }

    public static NotFoundException resultPieceNotFoundById() {
        return new NotFoundException("Contest Result Piece not found by id");
    }

    public static NotFoundException performanceNotFoundByUserAndId(String usercode, Long performanceId) {
        return new NotFoundException("Performance not found for user " + usercode + " with id " + performanceId);
    }

    public static NotFoundException personAliasNotFoundByIds(String slug, Long aliasId) {
        return new NotFoundException("Alias with id " + aliasId + " not found for person with slug " + slug);
    }

    public static NotFoundException pieceAliasNotFoundByIds(String slug, Long aliasId) {
        return new NotFoundException("Alias with id " + aliasId + " not found for piece with slug " + slug);
    }

    public static NotFoundException venueAliasNotFoundByIds(String slug, Long aliasId) {
        return new NotFoundException("Alias with id " + aliasId + " not found for venue with slug " + slug);
    }

    public static NotFoundException dateParseError(String contestEventDate) {
        return new NotFoundException("Can't parse date " + contestEventDate);
    }
}
