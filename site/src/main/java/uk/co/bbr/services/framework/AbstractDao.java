package uk.co.bbr.services.framework;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
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

    protected String escapeJson(String text) {
        return text.replace("'", "`");
    }
}


