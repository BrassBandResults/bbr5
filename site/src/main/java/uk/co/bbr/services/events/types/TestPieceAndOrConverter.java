package uk.co.bbr.services.events.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
