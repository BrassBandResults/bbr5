package uk.co.bbr.services.framework;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.co.bbr.services.bands.dao.BandDao;

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
}
