package ftn.siit.project.isspoject.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private String email;
    private String name;
    private String lastname;
    private String address;
    private String phoneNumber;
    private String photoUrl;
    private boolean isActive;
    private LocalDateTime suspendedSince;
    private List<String> favouriteOffers;
    private List<String> favouriteEvents;
    private List<String> attends;
    private List<String> notifications;

}

