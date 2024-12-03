package ftn.siit.project.isspoject.dto.event;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class ClosedEventDTO {

    private Integer id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer participants;
    private Boolean isPublic;
    private String place;
    private LocalDateTime date;
    private EventType eventType;
    private List<String> emails = new ArrayList<>();
    public ClosedEventDTO() {
        super();
        this.isPublic = false;
    }
    public ClosedEventDTO(Event event) {
        if (event != null) {
            this.id = event.getId();
            this.name = event.getName();
            this.description = event.getDescription();
            this.maxParticipants = event.getMaxParticipants();
            this.participants = event.getParticipants();
            this.isPublic = false;
            this.place = event.getPlace();
            this.date = event.getDate();
            this.eventType = event.getEventType();
        }
    }
}
