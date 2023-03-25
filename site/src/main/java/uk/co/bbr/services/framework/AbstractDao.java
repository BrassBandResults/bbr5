package uk.co.bbr.services.framework;

import lombok.Getter;
import lombok.Setter;
import uk.co.bbr.services.security.dao.BbrUserDao;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class AbstractDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="created", nullable=false)
    private LocalDateTime created = LocalDateTime.now();

    @Column(name="updated", nullable=false)
    private LocalDateTime updated = LocalDateTime.now();

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_by")
    private String updatedBy;
}


