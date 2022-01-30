package uk.co.bbr.services.framework;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class AbstractDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="CREATED", nullable=false)
    private LocalDateTime created = LocalDateTime.now();

    @Column(name="UPDATED", nullable=false)
    private LocalDateTime updated = LocalDateTime.now();
    
    @Column(name="OWNER_ID", nullable=false)
    private long createdBy;

    @Column(name="UPDATED_BY_ID", nullable=false)
    private long updatedBy;
}


