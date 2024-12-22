package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.EmailDetails;
import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.Provider;
import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ReservationRepository;
import ftn.siit.project.isspoject.service.external.EmailService;
import ftn.siit.project.isspoject.service.interfaces.ReservationService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = reservationRepository.findAll();
        if (reservations.isEmpty()) {
            throw new NotFoundException("No reservations found.");
        }
        return reservations;
    }

    @Override
    public Reservation findById(Integer id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + id));
    }

    @Override
    public List<Reservation> findByEventId(Integer eventId) {
        List<Reservation> reservations = reservationRepository.findReservationsByEventId(eventId);
        if (reservations.isEmpty()) {
            throw new NotFoundException("No reservations found for event ID: " + eventId);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByServiceId(Integer serviceId) {
        List<Reservation> reservations = reservationRepository.findReservationsByServiceId(serviceId);
//        if (reservations.isEmpty()) {
//            throw new NotFoundException("No reservations found for service ID: " + serviceId);
//        }
        return reservations;
    }

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null while saving.");
        }
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation save(NewReservationDTO reservationDTO) {
        if (reservationDTO == null) {
            throw new IllegalArgumentException("ReservationDTO cannot be null while saving.");
        }
        Reservation reservation = new Reservation();
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getEndTime());
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation addReservation(Event event, ftn.siit.project.isspoject.entity.Service service, Provider provider, Organizer organizer, NewReservationDTO dto, UserService userService) {
        checkIfClosed(dto.getStartTime(), dto.getEndTime(), provider, userService);
        if(service.getPreciseDuration() == 0) {
            checkReservationDuration(dto.getStartTime(), dto.getEndTime(), service);
        }
        checkAvailability(service.getId(), dto.getStartTime(), dto.getEndTime());

        Reservation reservation = new Reservation(dto,event,service);
        Reservation created = this.save(reservation);
        sendConfirmations(event,service,provider,organizer,dto);
        return created;
    }

    private void checkIfClosed(LocalDateTime start, LocalDateTime end, Provider provider, UserService userService) {
        boolean companyIsClosed = userService.overlapsWithClosedHours(start, end, provider.getOpeningTime(), provider.getClosingTime());
        if (companyIsClosed) {
            throw new IllegalArgumentException("Reservation time overlaps with provider's closed hours.");
        }
    }

    private void checkReservationDuration(LocalDateTime start, LocalDateTime end, ftn.siit.project.isspoject.entity.Service service) {
        long reservationDurationMinutes = Duration.between(start, end).toMinutes();
        int minDuration = service.getMinDuration();
        int maxDuration = service.getMaxDuration();

        if (reservationDurationMinutes < minDuration || reservationDurationMinutes > maxDuration) {
            throw new IllegalArgumentException("Reservation duration must be between "
                    + minDuration + " and " + maxDuration + " minutes.");
        }
    }
    private void checkAvailability(Integer serviceId, LocalDateTime start, LocalDateTime end) {
        boolean isAvailable = this.isAvailable(serviceId, start, end);
        if (!isAvailable) {
            throw new IllegalArgumentException("Service isn't available at given reservation time.");
        }
    }

    private boolean isAvailable(Integer serviceId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = this.findByServiceId(serviceId);
        // Checks for overlaps
        for (Reservation reservation : reservations) {
            System.out.println(reservation.getStartTime());
            System.out.println(start);
            if (overlaps(start, end, reservation.getStartTime(), reservation.getEndTime())) {
                return false;
            }
        }
        return true;
    }
    private void sendConfirmations(Event event, ftn.siit.project.isspoject.entity.Service service, Provider provider, Organizer organizer, NewReservationDTO dto) {
        String organizerEmail = generateOrganizersConfirmation(event,service, provider,organizer, dto);
        String providerEmail = generateProvidersConfirmation(event,service, provider,organizer, dto);
        String status1 = emailService.sendMail(organizer,"Service reservation confirmation!",organizerEmail);
        String status2 = emailService.sendMail(provider,"Service reservation confirmation!",providerEmail);
        System.out.println("Invitations sent to: " + organizer.getEmail() + " and "+ provider.getEmail()+"- Status1: " + status1+"- Status2: " + status2);
    }

    public String generateOrganizersConfirmation(Event event, ftn.siit.project.isspoject.entity.Service service, Provider provider, Organizer organizer, NewReservationDTO reservationDto) {
        return String.format(
                "Dear %s,\n\n" +
                        "This is a confirmation for your reservation of the service \"%s\" for the event \"%s\".\n\n" +
                        "Reservation Details:\n" +
                        "- Event Name: %s\n" +
                        "- Event Description: %s\n" +
                        "- Event Date & Time: %s at %s\n" +
                        "- Service Name: %s\n" +
                        "- Service Description: %s\n" +
                        "- Reservation Start Time: %s\n" +
                        "- Reservation End Time: %s\n\n" +
                        "Thank you for using our services! If you have any questions or need assistance, please contact us.\n\n" +
                        "Best regards,\n" +
                        "%s",
                organizer.getName(),
                service.getName(),
                event.getName(),
                event.getName(),
                event.getDescription(),
                event.getDate().toString(),
                event.getPlace(),
                service.getName(),
                service.getDescription(),
                reservationDto.getStartTime().toString(),
                reservationDto.getEndTime().toString(),
                provider.getCompanyName()
        );
    }
    public String generateProvidersConfirmation(Event event, ftn.siit.project.isspoject.entity.Service service, Provider provider, Organizer organizer, NewReservationDTO reservationDto) {
        return String.format(
                "Dear %s,\n\n" +
                        "This is a confirmation that your service \"%s\" has been reserved by the organizer \"%s\" for the event \"%s\".\n\n" +
                        "Reservation Details:\n" +
                        "- Event Name: %s\n" +
                        "- Event Description: %s\n" +
                        "- Event Date & Time: %s at %s\n" +
                        "- Service Name: %s\n" +
                        "- Service Description: %s\n" +
                        "- Reservation Start Time: %s\n" +
                        "- Reservation End Time: %s\n\n" +
                        "Thank you for providing your service! If you have any questions, please contact the organizer directly.\n\n" +
                        "Best regards,\n" +
                        "Eventure",
                provider.getName(),
                service.getName(),
                organizer.getName(),
                event.getName(),
                event.getName(),
                event.getDescription(),
                event.getDate().toString(),
                event.getPlace(),
                service.getName(),
                service.getDescription(),
                reservationDto.getStartTime().toString(),
                reservationDto.getEndTime().toString(),
                provider.getCompanyName()
        );
    }
    private boolean overlaps(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
