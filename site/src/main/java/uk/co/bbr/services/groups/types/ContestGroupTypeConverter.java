package uk.co.bbr.services.groups.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ContestGroupTypeConverter implements AttributeConverter<ContestGroupType, String> {
    @Override
    public String convertToDatabaseColumn(ContestGroupType groupType) {
        if (groupType == null) {
            return null;
        }
        return groupType.getCode();
    }

    @Override
    public ContestGroupType convertToEntityAttribute(String code) {
        return ContestGroupType.fromCode(code);
    }
}
