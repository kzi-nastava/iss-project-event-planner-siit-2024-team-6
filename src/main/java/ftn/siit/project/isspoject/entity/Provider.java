package ftn.siit.project.isspoject.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("Provider")
@EqualsAndHashCode(callSuper = true)
@Data
public class Provider extends User {

    @Column(nullable = false)
    private String companyEmail;

    @Column(nullable = false)
    private String companyName;

    private String companyAddress;
    private String description;

    @ElementCollection
    @CollectionTable(name = "provider_company_photos", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "photo_url")
    private List<String> companyPhotos;

    private String openingTime;
    private String closingTime;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Offer> myOffers;

    public boolean hasActiveServices() {
        return false;
    }
}

