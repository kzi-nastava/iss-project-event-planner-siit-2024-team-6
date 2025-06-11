package ftn.siit.project.isspoject.dto.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import ftn.siit.project.isspoject.entity.Offer;

public class PriceListItemDTO {
    private int offerId;
    private String offerName;
    private double offerPrice;
    private double offerDiscountPrice;
     @JsonProperty("isService")
    private boolean isService;

    public PriceListItemDTO(int offerId, String offerName, double offerPrice, double offerDiscountPrice) {
        this.offerId = offerId;
        this.offerName = offerName;
        this.offerPrice = offerPrice;
        this.offerDiscountPrice = offerDiscountPrice;
        this.isService = false;
    }

    public PriceListItemDTO(Offer o){
        this.offerId = o.getId();
        this.offerName = o.getName();
        this.offerPrice = o.getPrice();
        this.offerDiscountPrice = o.getSale();
        this.isService = o.isService();
    }
    public PriceListItemDTO() {}
    public int getOfferId() {
        return offerId;
    }
    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }
    public String getOfferName() {
        return offerName;
    }
    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }
    public double getOfferPrice() {
        return offerPrice;
    }
    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }
    public double getOfferDiscountPrice() {
        return offerDiscountPrice;
    }
    public void setOfferDiscountPrice(double offerDiscountPrice) {
        this.offerDiscountPrice = offerDiscountPrice;
    }
    @JsonProperty("isService")
    public boolean isService() {
        return isService;
    }
    @JsonProperty("isService")
    public void setService(boolean isService) {
        this.isService = isService;
    }
}
