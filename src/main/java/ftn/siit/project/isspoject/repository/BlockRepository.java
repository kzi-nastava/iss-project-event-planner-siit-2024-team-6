package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Block;
import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface BlockRepository extends JpaRepository<Block, Integer> {
    boolean existsByBlockerIdAndBlockedId(User blockerId, User blockedId);
}
