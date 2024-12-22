package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.dto.event.OrganizersEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>{
    @Query(value = "SELECT id, name, place, date FROM events WHERE organizer_id = :organizerId", nativeQuery = true)
    List<Object[]> findByOrganizerId(Integer organizerId);

    List<Event> findTop5ByOrderByDateAsc();

    @Query("SELECT e FROM User u JOIN u.attends e WHERE u.id = :userId")
    List<Event> findEventsByUserId(@Param("userId") Integer userId);

    @Query("SELECT e FROM Event e WHERE " +
            "(LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND " +
            "(LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')) OR :description IS NULL) AND " +
            "(LOWER(e.place) LIKE LOWER(CONCAT('%', :place, '%')) OR :place IS NULL) AND " +
            "(e.eventType = :eventType OR :eventType IS NULL) AND " +
            "(e.isPublic = :isPublic OR :isPublic IS NULL) AND " +
            "(e.date BETWEEN :startDate AND :endDate OR :startDate IS NULL OR :endDate IS NULL)")
    List<Event> searchEvents(String name, String description, String place, EventType eventType, Boolean isPublic, LocalDateTime startDate, LocalDateTime endDate);
}
