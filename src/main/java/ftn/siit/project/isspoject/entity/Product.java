package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.offer.NewOfferDTO;
import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("Product")
public class Product extends Offer{
    public Product(){super();}
    public Product(OfferDTO offerDTO, Category category) {super(offerDTO, category);}

    public Product(NewOfferDTO dto, Category category, List<EventType> eventTypes, Provider provider) {
        this.setStatus(dto.getStatus());
        this.setName(dto.getName());
        this.setDescription(dto.getDescription());
        this.setPrice(dto.getPrice());
        this.setSale(dto.getSale());
        this.setPhotos(dto.getPhotos());
        this.setIsVisible(dto.getIsVisible());
        this.setIsAvailable(dto.getIsAvailable());
        this.setIsDeleted(dto.getIsDeleted());
        this.setCategory(category);
        this.setEventTypes(eventTypes);
        this.setProvider(provider);
    }
}
