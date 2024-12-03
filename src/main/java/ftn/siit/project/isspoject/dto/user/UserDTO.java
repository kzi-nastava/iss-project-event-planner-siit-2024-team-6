package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.User;
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

