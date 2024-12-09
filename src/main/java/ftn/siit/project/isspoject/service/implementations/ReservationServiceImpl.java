package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.offer.NewReservationDTO;
import ftn.siit.project.isspoject.dto.offer.ReservationDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.OfferService;
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
    public List<Reservation> findByEvent(Event event) {
        return reservationRepository.findByEvent(event);
    }

    @Override
    public List<Reservation> findByService(OfferService service) {
        return reservationRepository.findByService(service);
    }


    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation save(NewReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setStartTime(reservationDTO.getStart());
        reservation.setEndTime(reservationDTO.getEnd());
        return reservationRepository.save(reservation);
    }
}
