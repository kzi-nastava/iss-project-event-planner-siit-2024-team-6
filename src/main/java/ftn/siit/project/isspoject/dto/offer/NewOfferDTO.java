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
    private String category;
    private List<EventTypeDTO> eventTypes;
    private String type; // product or service


    // Service fielsd
    private String specifics;
    private int minDuration;
    private int maxDuration;
    private int preciseDuration;
    private int latestReservation;
    private int latestCancelation;
    private boolean isReservationAutoApproved;

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
            this.category = offer.getCategory() != null ? offer.getCategory().getName() : null;

            if (offer instanceof Product) {
                this.type = "Product";
            } else if (offer instanceof OfferService) {
                this.type = "Service";
                OfferService offerService = (OfferService) offer;
                this.specifics = offerService.getSpecifics();
                this.minDuration = offerService.getMinDuration();
                this.maxDuration = offerService.getMaxDuration();
                this.preciseDuration = offerService.getPreciseDuration();
                this.latestReservation = offerService.getLatestReservation();
                this.latestCancelation = offerService.getLatestCancelation();
                this.isReservationAutoApproved = offerService.getIsReservationAutoApproved();
            }
        }
    }
}

