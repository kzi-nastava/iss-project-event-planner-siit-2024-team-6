package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
    List<Reservation> findAll();
    Reservation findById(Integer id);
    List<Reservation> findByEventId(Integer eventId);
    List<Reservation> findByServiceId(Integer serviceId);
    Reservation save(Reservation reservation);
    Reservation save(NewReservationDTO reservationDTO);
    Reservation addReservation(Event event, Service service, Provider provider, Organizer organizer, NewReservationDTO reservationDto, UserService userService);
}
