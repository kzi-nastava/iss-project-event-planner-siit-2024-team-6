package ftn.siit.project.isspoject.dto.offer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewReservationDTO {
    private Integer serviceId;
    private LocalDateTime start;
    private LocalDateTime end;

}
