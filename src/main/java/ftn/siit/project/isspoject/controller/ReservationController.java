package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.*;
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
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @PostMapping()
    public ResponseEntity<ReservationDTO> addReservation(@RequestBody NewReservationDTO dto) {
        logger.info("Received NewReservationDTO: {}", dto);

        if (dto == null) {
            logger.error("NewReservationDTO is null");
            return ResponseEntity.badRequest().build();
        }

        logger.info("Looking up Service with ID: {}", dto.getServiceId());
        Service service = serviceService.findById(dto.getServiceId());
        if (service == null) {
            logger.error("Service not found for ID: {}", dto.getServiceId());
            return ResponseEntity.badRequest().body(null);
        }

        logger.info("Looking up Event with ID: {}", dto.getEventId());
        Event event = eventService.findById(dto.getEventId());
        if (event == null) {
            logger.error("Event not found for ID: {}", dto.getEventId());
            return ResponseEntity.badRequest().body(null);
        }

        Integer providerId = service.getProvider() != null ? service.getProvider().getId() : null;
        logger.info("Looking up Provider with ID: {}", providerId);
        if (providerId == null) {
            logger.error("Service does not have a Provider assigned");
            return ResponseEntity.badRequest().body(null);
        }
        Provider provider = (Provider) userService.findById(providerId);
        if (provider == null) {
            logger.error("Provider not found for ID: {}", providerId);
            return ResponseEntity.badRequest().body(null);
        }

        logger.info("Looking up Organizer for Event ID: {}", dto.getEventId());
        Organizer organizer = organizerService.findByEventId(dto.getEventId());
        if (organizer == null) {
            logger.error("Organizer not found for Event ID: {}", dto.getEventId());
            return ResponseEntity.badRequest().body(null);
        }

        logger.info("Creating reservation with Event: {}, Service: {}, Provider: {}, Organizer: {}",
                event.getId(), service.getId(), provider.getId(), organizer.getId());

        Reservation created = reservationService.addReservation(event, service, provider, organizer, dto, userService);
        double price = service.getSale();
        if (price == 0) {
            price = service.getPrice();
        }
        budgetService.addNewItem(service.getCategory(), price, event.getBudget().getId());

        logger.info("Reservation created with ID: {}", created.getId());

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
        budgetService.removeItem(reservation.getEvent().getBudget().getId(), reservation.getOfferService().getCategory())
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
