package uk.co.bbr.services.framework.mixins;

public interface NameTools {
    default String simplifyName(String name) {
        return name.replaceAll(" +", " ").trim();
    }
}
