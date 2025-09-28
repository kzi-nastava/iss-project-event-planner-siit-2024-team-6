package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private BudgetService budgetService;
    @Autowired
    private OrganizerService organizerService;
    @Autowired
    private TokenUtils tokenUtils;

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @PostMapping()
    public ResponseEntity<ReservationDTO> addReservation(@RequestBody NewReservationDTO dto) {

        if (dto == null) {
            return ResponseEntity.badRequest().build();
        }

        if (dto.getServiceId() == null || dto.getEventId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            return ResponseEntity.badRequest().body(null);
        }

        Service service = serviceService.findById(dto.getServiceId());
        if (service == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Event event = eventService.findById(dto.getEventId());
        if (event == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Integer providerId = service.getProvider() != null ? service.getProvider().getId() : null;
        if (providerId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Provider provider = (Provider) userService.findById(providerId);
        if (provider == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Organizer organizer = organizerService.findByEventId(dto.getEventId());
        if (organizer == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Reservation created = reservationService.addReservation(event, service, provider, organizer, dto, userService);
        double price = service.getSale();
        if (price == 0) {
            price = service.getPrice();
        }
        budgetService.addNewItem(service.getCategory(), price, event.getBudget().getId());

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
        budgetService.removeItem(reservation.getEvent().getBudget().getId(), reservation.getOfferService().getCategory());
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

    @GetMapping("{offerId}/reserved")
    public ResponseEntity<Boolean> isPurchased(@PathVariable int offerId, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return ResponseEntity.ok(false);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = organizerService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.ok(false);
        }

        List<Reservation> reservations = reservationService.findByServiceId(offerId);
        if(reservations.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        for(Reservation reservation : reservations) {
            for(Event e: user.getMyEvents()){
                if (reservation.getEvent().getId().equals(e.getId()) && !reservation.isCanceled()) {
                    return new ResponseEntity<>(true, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

}
