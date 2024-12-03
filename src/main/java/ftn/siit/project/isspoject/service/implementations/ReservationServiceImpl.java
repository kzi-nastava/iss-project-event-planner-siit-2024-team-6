package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.ReservationDTO;
import ftn.siit.project.isspoject.entity.Reservation;
import ftn.siit.project.isspoject.service.interfaces.ReservationService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReservationServiceImpl implements ReservationService {

    //@Autowired
    //private ReservationRepository reservationRepository;

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
        return new Reservation();
    }
}
