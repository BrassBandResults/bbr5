package uk.co.bbr.services.pieces.types;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PieceCategoryConverter implements AttributeConverter<PieceCategory, String> {
    @Override
    public String convertToDatabaseColumn(PieceCategory category) {
        if (category == null) {
            return null;
        }
        return category.getCode();
    }

    @Override
    public PieceCategory convertToEntityAttribute(String code) {
        return PieceCategory.fromCode(code);
    }
}
