package ftn.siit.project.isspoject.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Provider extends User{
    private String companyEmail;
    private String companyName;
    private String companyAddress;
    private String description;
    private String[] companyPhotos;
    private String openingTime;
    private String closingTime;
    private List<Offer> myOffers;

    public Provider() {}
    public Provider(User user) {
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
    public boolean hasActiveServices() {
        return false;
    }
}
