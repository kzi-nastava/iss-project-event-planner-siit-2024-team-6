package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.offer.OfferDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("OfferService")
public class OfferService extends Offer{
    private String specifics;
    private Integer minDuration;
    private Integer maxDuration;
    private Integer preciseDuration;
    private Integer latestReservation;
    private Integer latestCancelation;

    public OfferService(){}

    public OfferService(OfferDTO dto, Category category){
        super(dto, category);
        this.specifics = dto.getSpecifics();
        this.minDuration = dto.getMinDuration();
        this.maxDuration = dto.getMaxDuration();
        this.preciseDuration = dto.getPreciseDuration();
        this.latestReservation = dto.getLatestReservation();
        this.latestCancelation = dto.getLatestCancelation();
    }

    public OfferService(Integer id, Status status, String name, String description, Double price, Double sale,
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
