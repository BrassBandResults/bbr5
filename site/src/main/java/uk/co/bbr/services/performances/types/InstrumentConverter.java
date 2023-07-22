package uk.co.bbr.services.performances.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
