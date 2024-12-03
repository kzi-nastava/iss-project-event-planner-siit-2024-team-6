package ftn.siit.project.isspoject.entity;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class OfferHistory {
    private Integer id;
    private List<Offer> offers;
    private List<ZonedDateTime> timestamps;
}
