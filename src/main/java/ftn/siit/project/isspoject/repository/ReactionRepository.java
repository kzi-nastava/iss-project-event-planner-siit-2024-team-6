package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Reaction;
import ftn.siit.project.isspoject.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer>{
    Page<Reaction> findByStatusAndIsDeletedFalse(Status status, Pageable pageable);

    @Query("SELECT r FROM Reaction r WHERE r.user.id = :userId")
    List<Reaction> findReactionsByUserId(@Param("userId") Integer userId);

    @Query("SELECT r FROM Reaction r WHERE r.event.id = :eventId")
    List<Reaction> findReactionsByEventId(@Param("eventId") Integer eventId);

    @Query("SELECT r FROM Reaction r WHERE r.offer.id = :offerId AND r.isDeleted = false AND r.status = :status")
    List<Reaction> findReactionsByOfferId(@Param("offerId") Integer offerId, @Param("status") Status status);

    @Query("SELECT r FROM Reaction r WHERE r.offer.provider.id = :providerId AND r.isDeleted = false AND r.status = :status")
    Page<Reaction> findReactionsForProvider(@Param("providerId") Integer providerId, @Param("status") Status status, Pageable pageable);
}
