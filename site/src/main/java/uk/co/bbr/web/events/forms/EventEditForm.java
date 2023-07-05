package uk.co.bbr.web.events.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class EventEditForm {
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    private String dateResolution;
    private String contest;
    private String notes;
    private String venue;
    private boolean noContest;
    private Long contestType;
}
