package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import jakarta.persistence.*;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "offer_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Status status;
    private String name;
    private String description;
    private Double price;
    private Double sale;
    @ElementCollection
    @CollectionTable(name = "offer_photos", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "photo_url")
    private List<String> photos;
    private Boolean isVisible;
    private Boolean isAvailable;
    private Boolean isDeleted;
    private LocalDateTime lastChanged;
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToMany
    @JoinTable(
            name = "offer_event_types",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "event_type_id")
    )
    private List<EventType> eventTypes;
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    public Offer() {}
    public Offer(
            Integer id,
            Status status,
            String name,
            String description,
            Double price,
            Double sale,
            List<String> photos,
            Boolean isVisible,
            Boolean isAvailable,
            Boolean isDeleted,
            LocalDateTime lastChanged,
            Category category,
            List<EventType> eventTypes) {
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
        this.category = category;
    }
    public Offer(NewOfferDTO dto, Category category) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.sale = dto.getSale();
        this.photos = dto.getPhotos();
        this.isVisible = dto.getIsVisible();
        this.isDeleted = dto.getIsDeleted();
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

