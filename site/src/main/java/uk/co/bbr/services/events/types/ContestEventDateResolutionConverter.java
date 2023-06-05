package uk.co.bbr.services.events.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ContestEventDateResolutionConverter implements AttributeConverter<ContestEventDateResolution, String> {
    @Override
    public String convertToDatabaseColumn(ContestEventDateResolution groupType) {
        if (groupType == null) {
            return null;
        }
        return groupType.getCode();
    }

    @Override
    public ContestEventDateResolution convertToEntityAttribute(String code) {
        return ContestEventDateResolution.fromCode(code);
    }
}
