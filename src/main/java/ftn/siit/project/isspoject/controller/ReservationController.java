package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/reservations/")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @PostMapping("add")
    public ResponseEntity<String> createReservation(@RequestBody ReservationDTO dto) {
        Reservation reservation = reservationService.save(dto);
        sendConfirmations(reservation);
        return ResponseEntity.ok("Service with id " + reservation.getServiceId() + " is reserved for event "+ reservation.getEventId());
    }

    private void sendConfirmations(Reservation reservation) {

    }
}
