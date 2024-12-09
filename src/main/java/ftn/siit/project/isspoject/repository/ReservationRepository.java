package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("SELECT r FROM Reservation r WHERE r.event.id = :eventId")
    List<Reservation> findReservationsByEventId(@Param("eventId") Integer eventId);

    @Query("SELECT r FROM Reservation r WHERE r.service.id = :eventId")
    List<Reservation> findReservationsByServiceId(@Param("eventId") Integer eventId);

}
