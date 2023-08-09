package uk.co.bbr.services.events.types;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ResultPositionTypeConverter implements AttributeConverter<ResultPositionType, String> {
    @Override
    public String convertToDatabaseColumn(ResultPositionType groupType) {
        if (groupType == null) {
            return null;
        }
        return groupType.getCode();
    }

    @Override
    public ResultPositionType convertToEntityAttribute(String code) {
        return ResultPositionType.fromCode(code);
    }
}
