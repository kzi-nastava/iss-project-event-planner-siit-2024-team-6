package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.entity.Service;
import ftn.siit.project.isspoject.service.interfaces.ReservationService;
import ftn.siit.project.isspoject.service.interfaces.ServiceService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
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
    @PostMapping()
    public ResponseEntity<ReservationDTO> addReservation(@RequestBody NewReservationDTO dto) {
        Provider provider = (Provider) userService.findById(dto.getService().getProvider().getId());
        System.out.println(provider.getOpeningTime());
        System.out.println(provider.getClosingTime());
        checkIfClosed(dto.getStart(), dto.getEnd(), provider);

        Service service = serviceService.findById(dto.getService().getId());
        checkReservationDuration(dto.getStart(), dto.getEnd(), service);
        checkAvailability(dto.getService().getId(), dto.getStart(), dto.getEnd());

        Reservation reservation = reservationService.save(dto);
        sendConfirmations(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReservationDTO(reservation));
    }

    private void checkIfClosed(LocalDateTime start, LocalDateTime end, Provider provider) {
        boolean companyIsClosed = userService.overlapsWithClosedHours(start, end, provider.getOpeningTime(), provider.getClosingTime());
        if (companyIsClosed) {
            throw new IllegalArgumentException("Reservation time overlaps with provider's closed hours.");
        }
    }

    private void checkReservationDuration(LocalDateTime start, LocalDateTime end, Service service) {
        long reservationDurationMinutes = Duration.between(start, end).toMinutes();
        int minDuration = service.getMinDuration();
        int maxDuration = service.getMaxDuration();

        if (reservationDurationMinutes < minDuration || reservationDurationMinutes > maxDuration) {
            throw new IllegalArgumentException("Reservation duration must be between "
                    + minDuration + " and " + maxDuration + " minutes.");
        }
    }
    private void checkAvailability(Integer serviceId, LocalDateTime start, LocalDateTime end) {
        boolean isAvailable = reservationService.isAvailable(serviceId, start, end);
        if (!isAvailable) {
            throw new IllegalArgumentException("Service isn't available at given reservation time.");
        }
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
