package uk.co.bbr.services.section.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.bbr.services.framework.AbstractDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="section")
public class SectionDao extends AbstractDao {

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="slug", nullable=false)
    private String slug;

    @Column(name="position", nullable=false)
    private int position;

    @Column(name="map_short_code")
    private String mapShortCode;

    @Column(name="translation_key")
    private String translationKey;
}
