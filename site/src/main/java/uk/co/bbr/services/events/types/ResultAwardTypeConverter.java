package uk.co.bbr.services.events.types;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ResultAwardTypeConverter implements AttributeConverter<ResultAwardType, String> {
    @Override
    public String convertToDatabaseColumn(ResultAwardType groupType) {
        if (groupType == null) {
            return null;
        }
        return groupType.getCode();
    }

    @Override
    public ResultAwardType convertToEntityAttribute(String code) {
        if (code == null || code.strip().length() == 0) {
            return null;
        }
        return ResultAwardType.fromCode(code);
    }
}
