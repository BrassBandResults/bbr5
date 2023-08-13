package uk.co.bbr.services.events.types;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TestPieceAndOrConverter implements AttributeConverter<TestPieceAndOr, String> {
    @Override
    public String convertToDatabaseColumn(TestPieceAndOr groupType) {
        if (groupType == null) {
            return null;
        }
        return groupType.getCode();
    }

    @Override
    public TestPieceAndOr convertToEntityAttribute(String code) {
        return TestPieceAndOr.fromCode(code);
    }
}
