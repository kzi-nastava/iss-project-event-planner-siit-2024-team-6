package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
//@Entity
public class Event {
//    @Id
    private int id;
    private String name;
    private String description;
    private int maxParticipants;
    private int participants = 0;
    private boolean isPublic;
    private String place;
    private LocalDateTime date;
    private EventType eventType;
    private List<Activity> activities;
}