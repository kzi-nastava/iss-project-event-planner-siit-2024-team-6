package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.ReservationDTO;
import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Reservation> findAll() {
        return List.of();
    }

    @Override
    public Reservation findById(Integer id) {
        return null;
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
        return null;
    }

    @Override
    public Reservation save(ReservationDTO reservationDTO) {
        return null;
    }
}
