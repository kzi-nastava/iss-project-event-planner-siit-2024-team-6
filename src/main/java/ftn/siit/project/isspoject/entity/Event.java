package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.event.ClosedEventDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer participants;
    private Boolean isPublic;
    private String place;
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Activity> activities;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_id", referencedColumnName = "id")
    private Budget budget;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "event_products",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
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