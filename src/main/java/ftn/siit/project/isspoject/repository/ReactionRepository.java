package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Reaction;
import ftn.siit.project.isspoject.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer>{
    List<Reaction> findByStatus(Status status);

    @Query("SELECT r FROM Reaction r WHERE r.user.id = :userId")
    List<Reaction> findReactionsByUserId(@Param("userId") Integer userId);

    @Query("SELECT r FROM Reaction r WHERE r.event.id = :eventId")
    List<Reaction> findReactionsByEventId(@Param("eventId") Integer eventId);

    @Query("SELECT r FROM Reaction r WHERE r.offer.id = :offerId")
    List<Reaction> findReactionsByOfferId(@Param("offerId") Integer offerId);
}
