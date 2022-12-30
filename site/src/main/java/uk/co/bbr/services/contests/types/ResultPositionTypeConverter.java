package uk.co.bbr.services.contests.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
