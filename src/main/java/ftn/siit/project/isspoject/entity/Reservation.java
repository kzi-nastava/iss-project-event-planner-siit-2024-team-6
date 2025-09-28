package ftn.siit.project.isspoject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private boolean isCanceled;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnore
    private Service offerService;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Reservation(){}
    public Reservation(NewReservationDTO dto, Event event, Service service){
        this.isCanceled = false;
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
        this.offerService = service;
        this.event = event;
    }
}
