package uk.co.bbr.services.band.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

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
        return BandStatus.fromCode(code);
    }
}
