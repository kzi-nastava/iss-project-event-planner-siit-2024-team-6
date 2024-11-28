package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.OfferDTO;
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
    private Provider provider;
    private List<EventType> eventTypes;

    public Offer() {}
    public Offer(Integer id, Status status, String name, String description, Double price, Double sale, List<String> photos, Boolean isVisible, Boolean isAvailable, Boolean isDeleted, LocalDateTime lastChanged, Category category, Provider provider, List<EventType> eventTypes) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sale = sale;
        this.photos = photos;
        this.isVisible = isVisible;
        this.isAvailable = isAvailable;
        this.isDeleted = isDeleted;
        this.lastChanged = lastChanged;
        this.category = category;
        this.provider = provider;
        this.eventTypes = eventTypes;
    }

    public Offer(OfferDTO dto, Category category) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.sale = dto.getSale();
        this.photos = dto.getPhotos();
        this.isVisible = dto.getIsVisible();
        this.isDeleted = dto.getIsDeleted();
        this.lastChanged = dto.getLastChanged();
        this.category = category;
    }
    public Offer toOffer(OfferDTO dto, Category category) {
        if (dto == null) {return null;}
        if (dto.getType().equals("Product")){
            return new Product(dto, category);
        }else{
            return new Service(dto, category);
        }
    }
}

