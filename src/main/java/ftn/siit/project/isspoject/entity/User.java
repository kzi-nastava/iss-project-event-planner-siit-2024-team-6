package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// Gen-ate getters, setters, constructors
@Data
//@Entity
public class User {
//    @Id
    private String email;
    private String password;
    private String photoUrl;
    private boolean isActive;
    private LocalDateTime suspendedSince;
    private String name;
    private String lastname;
    private String address;
    private String phoneNumber;
//    @ManyToMany
    private List<Offer> favouriteOffers;
//    @ManyToMany
    private List<Event> favouriteEvents;
//    @ManyToMany
    private List<Event> attends;
    private List<Notification> notifications;
}
