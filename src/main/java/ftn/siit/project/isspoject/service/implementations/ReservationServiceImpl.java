package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ReservationRepository;
import ftn.siit.project.isspoject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation findById(Integer id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + id));
    }

    @Override
    public List<Reservation> findByEventId(Integer eventId) {
        return List.of();
    }

    @Override
    public List<Reservation> findByServiceId(Integer serviceId) {
        return List.of();
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation save(NewReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setStart(reservationDTO.getStart());
        reservation.setEnd(reservationDTO.getEnd());
        return reservationRepository.save(reservation);
    }
}
