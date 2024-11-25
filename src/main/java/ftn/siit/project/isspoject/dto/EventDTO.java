package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.EventType;
import lombok.Data;

import java.time.LocalDateTime;

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
    private EventType eventType;
}
