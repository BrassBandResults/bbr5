package uk.co.bbr.services.framework.types;

public enum EntityType {
    CONTEST("entity-type.contest"),
    GROUP("entity-type.group"),
    ;

    private final String translationKey;

    EntityType(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }
}
