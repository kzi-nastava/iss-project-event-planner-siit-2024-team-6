package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.dto.event.OrganizersEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>{
//    @Query(value = "SELECT id, name, place, date FROM events WHERE organizer_id = :organizerId", nativeQuery = true)
//    List<Object[]> findByOrganizerId(Integer organizerId);

    List<Event> findTop5ByOrderByDateAsc();

    @Query("SELECT e FROM User u JOIN u.attends e WHERE u.id = :userId")
    List<Event> findEventsByUserId(@Param("userId") Integer userId);

    @Query("SELECT e FROM Event e WHERE e.id IN (" +
            "SELECT ev.id FROM Organizer o JOIN o.myEvents ev " +
            "WHERE o = :organizer AND ev.date > :now)")
    List<Event> findFutureEventsByOrganizer(@Param("organizer") Organizer organizer, @Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE e.id IN (SELECT ev.id FROM Organizer o JOIN o.myEvents ev WHERE o = :organizer)")
    List<Event> findByOrganizer(Organizer organizer);
    @Query("SELECT e FROM Event  e WHERE e.isDeleted = false")
    Page<Event> findByIsDeletedFalse(Pageable pageable);
}
