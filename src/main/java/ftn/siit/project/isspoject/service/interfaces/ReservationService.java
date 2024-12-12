package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Reservation;

import java.util.List;

public interface ReservationService {
    List<Reservation> findAll();
    Reservation findById(Integer id);
    List<Reservation> findByEventId(Integer eventId);
    List<Reservation> findByServiceId(Integer serviceId);
    Reservation save(Reservation reservation);
    Reservation save(NewReservationDTO reservationDTO);
}
