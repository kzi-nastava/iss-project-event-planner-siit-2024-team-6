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

    private String companyEmail;

    private String companyName;

    private String companyAddress;
    private String description;

    @ElementCollection
    @CollectionTable(name = "provider_company_photos", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "photo_url")
    private List<String> companyPhotos;

    private String openingTime;
    private String closingTime;

    public boolean hasActiveServices() {
        return false;
    }
}

