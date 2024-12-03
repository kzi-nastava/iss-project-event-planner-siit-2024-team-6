package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.event.ClosedEventDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
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
    private List<Activity> activities;
    private Budget budget;
    private List<Reservation> reservations;
    private List<Product> products;

    public Event() {}

    public Event(
            Integer id,
            String name,
            String description,
            Integer maxParticipants,
            Integer participants,
            Boolean isPublic,
            String place,
            LocalDateTime date,
            EventType eventType,
            List<Activity> activities,
            Budget budget,
            List<Reservation> reservations,
            List<Product> products) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.participants = participants;
        this.isPublic = isPublic;
        this.place = place;
        this.date = date;
        this.eventType = eventType;
        this.activities = activities;
        this.budget = budget;
        this.reservations = reservations;
        this.products = products;
    }
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
            this.products = new ArrayList<>();
            this.reservations = new ArrayList<>();
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
        this.products = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.activities = new ArrayList<>();
    }
}