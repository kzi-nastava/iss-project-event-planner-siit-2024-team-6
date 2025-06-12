package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Chat;
import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query("""
            SELECT c FROM Chat c 
            WHERE (c.participant1.id = :userId OR c.participant2.id = :userId) 
              AND c.isBlocked = false
            ORDER BY c.lastUpdated DESC
            """)
    List<Chat> findAllWithParticipant(@Param("userId") int userId);


    @Query("SELECT c FROM Chat c WHERE " +
            "(c.participant1.id = :userId1 AND c.participant2.id = :userId2) OR " +
            "(c.participant1.id = :userId2 AND c.participant2.id = :userId1)")
    Optional<Chat> findChatByParticipants(@Param("userId1") int userId1, @Param("userId2") int userId2);


}
