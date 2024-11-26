package ftn.siit.project.isspoject.entity;
import ftn.siit.project.isspoject.dto.OfferDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
