package ftn.siit.project.isspoject.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Organizer extends User{
    private List<Event> myEvents;

    public Organizer() {}
    public Organizer(User user) {
        if (user != null) {
            this.setId(user.getId());
            this.setEmail(user.getEmail());
            this.setName(user.getName());
            this.setLastname(user.getLastname());
            this.setAddress(user.getAddress());
            this.setPhoneNumber(user.getPhoneNumber());
            this.setPhotoUrl(user.getPhotoUrl());
            this.setIsActive(user.getIsActive());
            this.setSuspendedSince(user.getSuspendedSince());
        }
    }
    public boolean hasFutureEvents() {
        return false;
    }
}
