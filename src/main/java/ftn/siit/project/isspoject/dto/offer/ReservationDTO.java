package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.entity.Reservation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    private Integer serviceId;
    private LocalDateTime start;
    private Integer duration;

}
