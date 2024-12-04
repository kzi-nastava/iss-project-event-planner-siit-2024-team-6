package ftn.siit.project.isspoject.dto.reaction;

import ftn.siit.project.isspoject.entity.Reaction;
import lombok.Data;

@Data
public class ReactionDTO {
    private Integer id;
    private String text;
    private Integer rating;
    private Integer offerId;
    private Integer eventId;
    private Integer userId;

    public ReactionDTO(Reaction reaction) {
        this.id = reaction.getId();
        this.text = reaction.getText();
        this.rating = reaction.getRating();
        this.offerId = reaction.getOffer().getId();
        this.eventId = reaction.getEvent().getId();
        this.userId = reaction.getUser().getId();
    }
}
