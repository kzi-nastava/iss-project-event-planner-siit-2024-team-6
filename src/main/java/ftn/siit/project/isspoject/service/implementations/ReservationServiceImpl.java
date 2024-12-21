package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ReservationRepository;
import ftn.siit.project.isspoject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

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
        if (reservations.isEmpty()) {
            throw new NotFoundException("No reservations found for service ID: " + serviceId);
        }
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
        reservation.setStartTime(reservationDTO.getStart());
        reservation.setEndTime(reservationDTO.getEnd());
        return reservationRepository.save(reservation);
    }

    public boolean isAvailable(Integer serviceId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = this.findByServiceId(serviceId);

        // Checkss for overlaps
        for (Reservation reservation : reservations) {
            if (overlaps(start, end, reservation.getStartTime(), reservation.getEndTime())) {
                return false;
            }
        }
        return true;
    }
    private boolean overlaps(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
