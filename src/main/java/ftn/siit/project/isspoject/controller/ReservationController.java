package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/reservations/")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<ReservationDTO> addReservation(@RequestBody NewReservationDTO dto) {
        Reservation reservation = reservationService.save(dto);
        sendConfirmations(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReservationDTO(reservation));
    }

    @PutMapping("{id}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Integer id, @RequestBody NewReservationDTO dto) {
        Reservation existingReservation = reservationService.findById(id);

        existingReservation.setStartTime(dto.getStart());
        existingReservation.setEndTime(dto.getEnd());
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

    private void sendConfirmations(Reservation reservation) {

    }
}
