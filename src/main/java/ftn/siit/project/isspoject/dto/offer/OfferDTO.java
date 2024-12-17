package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.dto.category.NewCategoryDTO;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Product;
import ftn.siit.project.isspoject.entity.Service;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OfferDTO {

    private Integer id;
    private Status status;
    private String name;
    private String description;
    private Double price;
    private Double sale;
    private List<String> photos;
    private Boolean isVisible;
    private Boolean isAvailable;
    private Boolean isDeleted;
    private String category;
    private LocalDateTime lastChanged;
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

    public OfferDTO() {
    }

    public OfferDTO(Offer offer) {
        if (offer != null) {
            this.id = offer.getId();
            this.status = offer.getStatus() != null ? Status.valueOf(offer.getStatus().toString()) : null;
            this.name = offer.getName();
            this.description = offer.getDescription();
            this.price = offer.getPrice();
            this.sale = offer.getSale();
            this.photos = offer.getPhotos();

            this.isVisible = offer.getIsVisible();
            this.isAvailable = offer.getIsAvailable();
            this.isDeleted = offer.getIsDeleted();
            this.eventTypes = offer.getEventTypes().stream().map(EventTypeDTO::new).toList();
            this.category = offer.getCategory().getName();
            if (offer instanceof Product) {
                this.type = "Product";
            } else if (offer instanceof Service) {
                this.type = "Service";
                Service offerService = (Service) offer;
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

