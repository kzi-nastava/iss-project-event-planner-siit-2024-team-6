package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.Offer;
import lombok.Data;

@Data
public class PriceListOfferDTO {
    Integer id;
    String name;
    Double price;
    Double sale;
    Double salePrice;

    public PriceListOfferDTO(Offer offer) {
        this.id = offer.getId();
        this.name = offer.getName();
        this.price = offer.getPrice();
        this.sale = offer.getSale();
        this.salePrice = this.price * (100 - this.sale);
    }
}
