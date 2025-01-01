package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Integer> {
    @Query("SELECT o FROM Organizer o JOIN o.myEvents e WHERE e.id = :eventId")
    Organizer findOrganizerByEventId(@Param("eventId") Integer eventId);

}
