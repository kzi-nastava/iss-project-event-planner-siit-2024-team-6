package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.entity.Offer;
import lombok.Data;

@Data
public class NewPriceListItemDTO {
    Double price;
    Double salePrice;

    NewPriceListItemDTO() {}
    NewPriceListItemDTO(Offer offer) {
        this.price = offer.getPrice();
        this.salePrice = offer.getPrice();
    }

}
