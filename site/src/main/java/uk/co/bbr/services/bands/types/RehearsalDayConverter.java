package uk.co.bbr.services.bands.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RehearsalDayConverter implements AttributeConverter<RehearsalDay, Integer> {
    @Override
    public Integer convertToDatabaseColumn(RehearsalDay category) {
        if (category == null) {
            return null;
        }
        return category.getCode();
    }

    @Override
    public RehearsalDay convertToEntityAttribute(Integer code) {
        return RehearsalDay.fromCode(code);
    }
}
