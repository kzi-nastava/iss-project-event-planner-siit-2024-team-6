package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Product extends Offer{
    public Product(){super();}
    public Product(OfferDTO offerDTO, Category category) {super(offerDTO, category);}
}
