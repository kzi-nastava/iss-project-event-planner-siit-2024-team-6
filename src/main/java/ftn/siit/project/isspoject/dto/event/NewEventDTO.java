package ftn.siit.project.isspoject.dto.event;

import ftn.siit.project.isspoject.entity.Event;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class NewEventDTO {
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer participants;
    private Boolean isPublic;
    private String place;
    private LocalDateTime date;
    private Integer eventTypeId;

    public NewEventDTO() {}
    public NewEventDTO(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        this.name = event.getName();
        this.description = event.getDescription();
        this.maxParticipants = event.getMaxParticipants();
        this.participants = event.getParticipants();
        this.isPublic = event.getIsPublic();
        this.place = event.getPlace();
        this.date = event.getDate();
    }
}
