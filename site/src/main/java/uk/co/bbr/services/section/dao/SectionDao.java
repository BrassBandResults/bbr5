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
@Table(name="SECTION")
public class SectionDao extends AbstractDao {

    @Column(name="NAME", nullable=false)
    private String name;

    @Column(name="SLUG", nullable=false)
    private String slug;

    @Column(name="POSITION", nullable=false)
    private int position;

    @Column(name="MAP_SHORT_CODE")
    private String mapShortCode;
}
