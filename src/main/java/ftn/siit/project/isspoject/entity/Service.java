package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
//@Entity
public class Service extends Offer{

    private String specifics;
    private int minDuration;
    private int maxDuration;
    private int preciseDuration;
    private int latestReservation;
    private int latestCancelation;
    public Service(){}
    public Service(OfferDTO dto, Category category){
        super(dto, category);
        this.specifics = dto.getSpecifics();
        this.minDuration = dto.getMinDuration();
        this.maxDuration = dto.getMaxDuration();
        this.preciseDuration = dto.getPreciseDuration();
        this.latestReservation = dto.getLatestReservation();
        this.latestCancelation = dto.getLatestCancelation();
    }

    public Service(Integer id, Status status, String name, String description, Double price, Double sale,
                   List<String> photos, Boolean isVisible, Boolean isAvailable, Boolean isDeleted,
                   LocalDateTime lastChanged, Category category, List<EventType> eventTypes,
                   String specifics, int minDuration, int maxDuration, int preciseDuration,
                   int latestReservation, int latestCancelation) {
        super(id, status, name, description, price, sale, photos, isVisible, isAvailable, isDeleted, lastChanged, category, eventTypes);
        this.specifics = specifics;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.preciseDuration = preciseDuration;
        this.latestReservation = latestReservation;
        this.latestCancelation = latestCancelation;
    }
}
