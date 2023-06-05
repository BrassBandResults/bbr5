package uk.co.bbr.services.events.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
        if (code == null || code.trim().length() == 0) {
            return null;
        }
        return ResultAwardType.fromCode(code);
    }
}
