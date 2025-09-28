package ftn.siit.project.isspoject.entity;
import ftn.siit.project.isspoject.dto.reaction.NewReactionDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reactions")
public class Reaction {
    @Id
    @GeneratedValue
    private Integer id;
    private String text;
    private Integer rating;
    private Status status;
    @ManyToOne(optional = true)
    @JoinColumn(name = "offer_id", referencedColumnName = "id")
    private Offer offer;
    @ManyToOne(optional = true)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
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
