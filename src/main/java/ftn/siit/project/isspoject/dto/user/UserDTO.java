package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.notification.NotificationDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {
    private Integer id;
    private String email;
    private String name;
    private String lastname;
    private String address;
    private String phoneNumber;
    private String photoUrl;
    private boolean isActive;

    private LocalDateTime suspendedSince;
    private List<OfferDTO> favouriteOffers;
    private List<EventDTO> favouriteEvents;
    private List<EventDTO> attends;
    private List<NotificationDTO> notifications;

    public UserDTO(User savedUser) {
    }

    public UserDTO() {

    }

    public User toUser() {
        User user = new User();
        user.setEmail(this.email);
        user.setName(this.name);
        user.setLastname(this.lastname);
        user.setAddress(this.address);
        user.setPhoneNumber(this.phoneNumber);
        user.setPhotoUrl(this.photoUrl);
        user.setIsActive(this.isActive);
        user.setSuspendedSince(this.suspendedSince);
        return user;
    }
}

