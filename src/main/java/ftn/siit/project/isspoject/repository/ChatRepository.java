package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Chat;
import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query("""
    SELECT c FROM Chat c 
    WHERE (c.participant1.id = :userId OR c.participant2.id = :userId) 
      AND c.isBlocked = false
    """)
    List<Chat> findAllWithParticipant(@Param("userId") int userId);


}
