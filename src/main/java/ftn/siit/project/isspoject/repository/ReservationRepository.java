package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.OfferService;
import ftn.siit.project.isspoject.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
    List<Reservation> findByEventId(Integer eventId);
    List<Reservation> findByServiceId(Integer serviceId);
}
