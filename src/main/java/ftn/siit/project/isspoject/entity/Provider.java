package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.util.List;

@Data
public class Provider {
    private String companyEmail;
    private String companyName;
    private String companyAddress;
    private String description;
    private String[] companyPhotos;
    private String openingTime;
    private String closingTime;
    private List<Offer> myOffers;
}
