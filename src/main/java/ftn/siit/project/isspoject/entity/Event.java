package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
//@Entity
public class Event {
//    @Id
    private Integer id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer participants;
    private Boolean isPublic;
    private String place;
    private LocalDateTime date;
    private EventType eventType;
    private Organizer organizer;
    private List<Activity> activities;
}