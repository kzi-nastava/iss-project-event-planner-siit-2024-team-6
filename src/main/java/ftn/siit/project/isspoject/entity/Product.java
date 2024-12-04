package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("Product")
public class Product extends Offer{
    public Product(){super();}
    public Product(OfferDTO offerDTO, Category category) {super(offerDTO, category);}
}
