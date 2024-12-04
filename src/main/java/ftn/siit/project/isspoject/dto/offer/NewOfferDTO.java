package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.entity.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewOfferDTO {
    private Status status;
    private String name;
    private String description;
    private Double price;
    private Double sale;
    private List<String> photos;
    private Boolean isVisible;
    private Boolean isAvailable;
    private Boolean isDeleted;
    private LocalDateTime lastChanged;
    private Category category;
    private List<EventTypeDTO> eventTypes;

    private String type; // product or service


    // Service fielsd
    private String specifics;
    private int minDuration;
    private int maxDuration;
    private int preciseDuration;
    private int latestReservation;
    private int latestCancelation;

    public NewOfferDTO() {}

    public NewOfferDTO(Offer offer) {
        if (offer != null) {
            this.status = offer.getStatus() != null ? Status.valueOf(offer.getStatus().toString()) : null;
            this.name = offer.getName();
            this.description = offer.getDescription();
            this.price = offer.getPrice();
            this.sale = offer.getSale();
            this.photos = offer.getPhotos();
            this.isVisible = offer.getIsVisible();
            this.isAvailable = offer.getIsAvailable();
            this.isDeleted = offer.getIsDeleted();
            this.lastChanged = offer.getLastChanged();
            this.category = offer.getCategory() != null ? offer.getCategory() : null;

            if (offer instanceof Product) {
                this.type = "Product";
            } else if (offer instanceof Service) {
                this.type = "Service";
                Service service = (Service) offer;
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

