package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.entity.Service;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewReservationDTO {
    private Integer serviceId;
    private Integer eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
