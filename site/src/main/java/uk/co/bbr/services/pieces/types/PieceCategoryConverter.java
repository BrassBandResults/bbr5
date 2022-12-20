package uk.co.bbr.services.pieces.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
