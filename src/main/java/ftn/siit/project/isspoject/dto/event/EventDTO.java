package ftn.siit.project.isspoject.dto.event;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer participants;
    private Boolean isPublic;
    private String place;
    private LocalDateTime date;
    private Integer eventTypeId;
    private List<String> photos;

    public EventDTO() {}
    public EventDTO(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.maxParticipants = event.getMaxParticipants();
        this.participants = event.getParticipants();
        this.isPublic = event.getIsPublic();
        this.place = event.getPlace();
        this.date = event.getDate();
        this.photos = event.getPhotos();
    }
}
