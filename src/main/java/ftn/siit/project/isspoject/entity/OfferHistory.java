package ftn.siit.project.isspoject.entity;
import java.time.ZonedDateTime;
import java.util.List;

public class OfferHistory {
    Integer id;
    List<Offer> offers;
    List<ZonedDateTime> timestamps; 
}
