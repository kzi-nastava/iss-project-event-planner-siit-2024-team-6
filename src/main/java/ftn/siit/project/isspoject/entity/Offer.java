package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
//@Entity
//Inheritance(strategy = InheritanceType.JOINED)
public class Offer {
//    @Id
    private Integer id;
    private Status status;
    private String name;
    private String description;
    private Double price;
    private Double sale;
//    @ElementCollection
    //@CollectionTable(name = "offer_photos", joinColumns = @JoinColumn(name = "offer_id"))
    //@Column(name = "photo_url")
    private List<String> photos;
    private Boolean isVisible;
    private Boolean isAvailable;
    private Boolean isDeleted;
    private LocalDateTime lastChanged;
    private Category category;
}

