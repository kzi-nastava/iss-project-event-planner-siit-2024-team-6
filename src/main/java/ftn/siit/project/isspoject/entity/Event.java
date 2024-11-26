package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.ClosedEventDTO;
import ftn.siit.project.isspoject.dto.EventDTO;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Budget budget;

    public Event() {}
    public Event(EventDTO eventDTO) {
        if (eventDTO != null) {
            this.id = eventDTO.getId();
            this.name = eventDTO.getName();
            this.description = eventDTO.getDescription();
            this.maxParticipants = eventDTO.getMaxParticipants();
            this.participants = eventDTO.getParticipants();
            this.isPublic = eventDTO.getIsPublic();
            this.place = eventDTO.getPlace();
            this.date = eventDTO.getDate();
            this.eventType = eventDTO.getEventType();
            this.organizer = new Organizer();
            this.activities = new ArrayList<>();
        }
    }
    public Event(ClosedEventDTO eventDTO) {
        if (eventDTO == null) {
            throw new IllegalArgumentException("ClosedEventDTO cannot be null");
        }

        this.id = eventDTO.getId();
        this.name = eventDTO.getName();
        this.description = eventDTO.getDescription();
        this.maxParticipants = eventDTO.getMaxParticipants();
        this.participants = eventDTO.getParticipants();
        this.isPublic = eventDTO.getIsPublic();
        this.place = eventDTO.getPlace();
        this.date = eventDTO.getDate();
        this.eventType = eventDTO.getEventType();
        this.organizer = new Organizer();
        this.activities = new ArrayList<>();
    }
}