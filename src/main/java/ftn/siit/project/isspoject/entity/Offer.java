package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
//@Entity
public class Offer {
//    @Id
    private int id;
    private Status status;
    private String name;
    private String description;
    private double price;
    private double sale;
//    @ElementCollection
    //@CollectionTable(name = "offer_photos", joinColumns = @JoinColumn(name = "offer_id"))
    //@Column(name = "photo_url")
    private List<String> photos;
    private boolean isVisible;
    private boolean isAvailable;
    private boolean isDeleted;
    private LocalDateTime lastChanged;
    private Category category;
}

