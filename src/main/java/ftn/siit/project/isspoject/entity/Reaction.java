package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.reaction.NewReactionDTO;
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

    public Reaction() {}
    public Reaction(NewReactionDTO dto){
        this.text = dto.getText();
        this.rating = dto.getRating();
        this.status = Status.ACCEPTED;
        this.isDeleted = false;
    }
}
