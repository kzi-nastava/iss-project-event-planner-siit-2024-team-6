package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface BlockRepository extends JpaRepository<Block, Integer> {
    @Query("SELECT COUNT(b) > 0 FROM Block b WHERE b.blockerId.id = :blockerId AND b.blockedId.id = :blockedId")
    boolean existsByBlockerIdAndBlockedId(@Param("blockerId") Integer blockerId, @Param("blockedId") Integer blockedId);

}
