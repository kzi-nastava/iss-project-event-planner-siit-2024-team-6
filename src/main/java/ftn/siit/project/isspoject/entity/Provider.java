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

    public boolean hasActiveServices() {
        return false;
    }
}
