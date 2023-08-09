package uk.co.bbr.services.performances.types;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InstrumentConverter implements AttributeConverter<Instrument, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Instrument category) {
        if (category == null) {
            return null;
        }
        return category.getCode();
    }

    @Override
    public Instrument convertToEntityAttribute(Integer code) {
        return Instrument.fromCode(code);
    }
}
