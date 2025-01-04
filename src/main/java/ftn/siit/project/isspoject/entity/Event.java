package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.NewClosedEventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "events")
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
    private Double rating;
    @ElementCollection
    @CollectionTable(name = "event_photos", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "photo_url")
    private List<String> photos;
    @ManyToOne
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private List<Activity> eventActivities;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_id", referencedColumnName = "id")
    private Budget budget;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "event_products",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;
    private Boolean isDeleted;

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
        this.eventActivities = activities;
        this.budget = budget;
        this.products = products;
        this.rating = 0.0;
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
            this.products = new ArrayList<>();
            this.eventActivities = new ArrayList<>();
        }
    }
    public Event(NewEventDTO eventDTO) {
        if (eventDTO != null) {
            this.name = eventDTO.getName();
            this.description = eventDTO.getDescription();
            this.maxParticipants = eventDTO.getMaxParticipants();
            this.participants = eventDTO.getParticipants();
            this.isPublic = eventDTO.getIsPublic();
            this.place = eventDTO.getPlace();
            this.date = eventDTO.getDate();
            this.products = new ArrayList<>();
            this.eventActivities = new ArrayList<>();
        }
    }
    public Event(NewClosedEventDTO eventDTO) {
        if (eventDTO == null) {
            throw new IllegalArgumentException("NewClosedEventDTO cannot be null");
        }

        this.name = eventDTO.getName();
        this.description = eventDTO.getDescription();
        this.maxParticipants = eventDTO.getMaxParticipants();
        this.participants = 0;
        this.isPublic = false;
        this.place = eventDTO.getPlace();
        this.date = eventDTO.getDate();
        this.products = new ArrayList<>();
        this.eventActivities = new ArrayList<>();
    }
}