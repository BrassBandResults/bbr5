package uk.co.bbr.services.bands.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class BandStatusConverter implements AttributeConverter<BandStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BandStatus category) {
        if (category == null) {
            return null;
        }
        return category.getCode();
    }

    @Override
    public BandStatus convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }
        return BandStatus.fromCode(code);
    }
}
