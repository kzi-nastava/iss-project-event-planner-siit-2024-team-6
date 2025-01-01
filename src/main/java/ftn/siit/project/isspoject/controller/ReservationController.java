package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/reservations/")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserService userService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private EventService eventService;
    @Autowired
    private OrganizerService organizerService;

    @PostMapping()
    public ResponseEntity<ReservationDTO> addReservation(@RequestBody NewReservationDTO dto) {
        Service service = serviceService.findById(dto.getServiceId());
        if (service == null) {
            throw new IllegalArgumentException("Service not found while creating reservation");
        }
        Event event = eventService.findById(dto.getEventId());
        if (event == null) {
            throw new IllegalArgumentException("Event not found while creating reservation");
        }
        Provider provider = (Provider) userService.findById(service.getProvider().getId());
        if (provider == null) {
            throw new IllegalArgumentException("Provider of service not found while creating reservation");
        }
        Organizer organizer = organizerService.findByEventId(dto.getEventId());
        if (organizer == null) {
            throw new IllegalArgumentException("Organizer of event not found while creating reservation");
        }
        Reservation created = reservationService.addReservation(event,service,provider,organizer,dto,userService);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReservationDTO(created));
    }



    @PutMapping("{id}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Integer id, @RequestBody NewReservationDTO dto) {
        Reservation existingReservation = reservationService.findById(id);

        existingReservation.setStartTime(dto.getStartTime());
        existingReservation.setEndTime(dto.getEndTime());
        Reservation updated = reservationService.save(existingReservation);

        return ResponseEntity.ok(new ReservationDTO(updated));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer id) {
        Reservation reservation = reservationService.findById(id);
        reservation.setCanceled(true);
        reservationService.save(reservation);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAll() {
        List<Reservation> reservations = reservationService.findAll();
        List<ReservationDTO> reservationDTOs = reservations.stream()
                .map(ReservationDTO::new)
                .toList();

        return ResponseEntity.ok(reservationDTOs);
    }

    @GetMapping("{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Integer id) {
        Reservation reservation = reservationService.findById(id);
        return ResponseEntity.ok(new ReservationDTO(reservation));
    }

}
