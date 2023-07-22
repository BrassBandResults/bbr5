package uk.co.bbr.services.performances.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PerformanceStatusConverter implements AttributeConverter<PerformanceStatus, String> {
    @Override
    public String convertToDatabaseColumn(PerformanceStatus category) {
        if (category == null) {
            return null;
        }
        return category.getCode();
    }

    @Override
    public PerformanceStatus convertToEntityAttribute(String code) {
        return PerformanceStatus.fromCode(code);
    }
}
