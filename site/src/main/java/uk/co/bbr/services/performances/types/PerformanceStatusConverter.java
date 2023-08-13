package uk.co.bbr.services.performances.types;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
