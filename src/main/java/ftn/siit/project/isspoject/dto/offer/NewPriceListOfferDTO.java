package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.entity.Offer;
import lombok.Data;

@Data
public class NewPriceListOfferDTO {
    String name;
    Double price;
    Double sale;
    Double salePrice;

    NewPriceListOfferDTO() {}
    NewPriceListOfferDTO(Offer offer) {
        this.name = offer.getName();
        this.price = offer.getPrice();
        this.sale = offer.getSale();
        this.salePrice = this.price * (100 - this.sale)/100;
    }

}
