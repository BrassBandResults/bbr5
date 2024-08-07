package uk.co.bbr.services.sections.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="section")
public class SectionDao extends AbstractDao implements NameTools {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="position", nullable=false)
    @Setter
    private int position;

    @Column(name="map_short_code")
    private String mapShortCode;

    @Column(name="translation_key")
    private String translationKey;

    public void setName(String name){
        if (name == null) {
            this.name = null;
        } else {
            this.name = simplifySectionName(name);
        }
    }

    public void setSlug(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.slug = value;
    }

    public void setMapShortCode(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.mapShortCode = value;
    }

    public void setTranslationKey(String value) {
        if (value != null) {
            value = value.strip();
        }
        this.translationKey = value;
    }
}
