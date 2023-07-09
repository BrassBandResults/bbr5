package uk.co.bbr.services.framework.types;

import java.util.stream.Stream;

public enum EntityType {
    PERSON("people", "entity-type.person"),
    CONTEST("contests", "entity-type.contest"),
    GROUP("contest-groups", "entity-type.contest-group"),
    VENUE("venues","entity-type.venue"),
    BAND("bands", "entity-type.band"),
    PIECE("pieces", "entity-type.piece"),
    TAG("tags", "entity-type.tags"),
    ;

    private final String offset;
    private final String translationKey;

    EntityType(String offset, String translationKey) {
        this.offset = offset;
        this.translationKey = translationKey;
    }

    public String getOffset() {
        return this.offset;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }

    public static EntityType fromOffset(String offset) {
        return Stream.of(EntityType.values())
                .filter(c -> c.getOffset().equals(offset))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
