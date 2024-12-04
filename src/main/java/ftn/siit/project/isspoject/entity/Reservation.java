package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "offer_service_id", nullable = false)
    private OfferService service;
    private boolean isCanceled;
    private LocalDateTime start;
    private LocalDateTime end;
}
