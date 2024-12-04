package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Product;
import ftn.siit.project.isspoject.entity.OfferService;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OfferDTO {

    private Integer id;
    private String status;
    private String name;
    private String description;
    private Double price;
    private Double sale;
    private List<String> photos;
    private Boolean isVisible;
    private Boolean isAvailable;
    private Boolean isDeleted;
    private LocalDateTime lastChanged;
    private String category;

    private String type; // product or service


    // Service fielsd
    private String specifics;
    private int minDuration;
    private int maxDuration;
    private int preciseDuration;
    private int latestReservation;
    private int latestCancelation;

    public OfferDTO() {}

    public OfferDTO(Offer offer) {
        if (offer != null) {
            this.id = offer.getId();
            this.status = offer.getStatus() != null ? offer.getStatus().toString() : null;
            this.name = offer.getName();
            this.description = offer.getDescription();
            this.price = offer.getPrice();
            this.sale = offer.getSale();
            this.photos = offer.getPhotos();
            this.isVisible = offer.getIsVisible();
            this.isAvailable = offer.getIsAvailable();
            this.isDeleted = offer.getIsDeleted();
            this.lastChanged = offer.getLastChanged();
            this.category = offer.getCategory() != null ? offer.getCategory().getName() : null;

            if (offer instanceof Product) {
                this.type = "Product";
            } else if (offer instanceof OfferService) {
                this.type = "Service";
                OfferService service = (OfferService) offer;
                this.specifics = service.getSpecifics();
                this.minDuration = service.getMinDuration();
                this.maxDuration = service.getMaxDuration();
                this.preciseDuration = service.getPreciseDuration();
                this.latestReservation = service.getLatestReservation();
                this.latestCancelation = service.getLatestCancelation();
            }
        }
    }
}

