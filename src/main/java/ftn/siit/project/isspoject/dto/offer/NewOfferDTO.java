package ftn.siit.project.isspoject.dto.offer;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class NewOfferDTO {
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
}
