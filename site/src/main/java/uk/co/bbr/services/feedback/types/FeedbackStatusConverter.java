package uk.co.bbr.services.feedback.types;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FeedbackStatusConverter implements AttributeConverter<FeedbackStatus, String> {
    @Override
    public String convertToDatabaseColumn(FeedbackStatus category) {
        if (category == null) {
            return null;
        }
        return category.getCode();
    }

    @Override
    public FeedbackStatus convertToEntityAttribute(String code) {
        return FeedbackStatus.fromCode(code);
    }
}
