package uk.co.bbr.services.region.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.band.dao.BandDao;
import uk.co.bbr.services.section.dao.SectionDao;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LinkSectionDto {
    private final SectionDao section;
    private final List<BandDao> bands = new ArrayList<>();

    public LinkSectionDto(SectionDao section) {
        this.section = section;
    }

    public String getTranslationKey() {
        return this.section.getTranslationKey();
    }

    public int getPosition() {
        return this.section.getPosition();
    }
}
