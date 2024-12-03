package ftn.siit.project.isspoject.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
//@Entity
public class Reaction {
    //@Id
    private Integer id;
    private String text;
    private Integer rating;
    private Status status;
    private Offer offer;
    private Event event;
    private User user;
    private boolean isDeleted = false;
}
