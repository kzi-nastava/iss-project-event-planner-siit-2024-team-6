package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.entity.Reservation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    private Integer id;
    private Integer serviceId;
    private LocalDateTime start;
    private LocalDateTime end;

    public ReservationDTO() {}
    public ReservationDTO(Reservation reservation) {
        this.id = reservation.getId();
        this.serviceId = reservation.getService().getId();
        this.start = reservation.getStartTime();
        this.end = reservation.getEndTime();
    }
}
