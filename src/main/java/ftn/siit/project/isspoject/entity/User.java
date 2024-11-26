package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class User {
    private Integer id;
    private String email;
    private String password;
    private String photoUrl;
    private Boolean isActive;
    private LocalDateTime suspendedSince;
    private String name;
    private String lastname;
    private String address;
    private String phoneNumber;
    private List<Offer> favouriteOffers;
    private List<Event> favouriteEvents;
    private List<Event> attends;
    private List<Notification> notifications;
}
