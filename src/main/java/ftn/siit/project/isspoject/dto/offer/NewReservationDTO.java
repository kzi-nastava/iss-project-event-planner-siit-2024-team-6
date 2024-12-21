package ftn.siit.project.isspoject.dto.offer;

import ftn.siit.project.isspoject.entity.Service;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewReservationDTO {
    private Service service;
    private LocalDateTime start;
    private LocalDateTime end;

}
