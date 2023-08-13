package uk.co.bbr.services.events.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.bbr.services.framework.AbstractDao;
import uk.co.bbr.services.framework.mixins.NameTools;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Entity
@NoArgsConstructor
@Table(name="contest_result_award_type")
public class ContestResultAwardTypeDao extends AbstractDao implements NameTools {

    @Column(name="name", nullable=false)
    private String name;
}
