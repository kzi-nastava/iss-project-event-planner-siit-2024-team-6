package ftn.siit.project.isspoject.dto.reaction;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Offer;
import ftn.siit.project.isspoject.entity.Status;
import ftn.siit.project.isspoject.entity.User;
import lombok.Data;

@Data
public class NewReactionDTO {
    private String text;
    private Integer rating;
    private Integer offerId;
    private Integer eventId;
}
