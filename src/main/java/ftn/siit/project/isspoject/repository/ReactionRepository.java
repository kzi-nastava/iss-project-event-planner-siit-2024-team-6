package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Reaction;
import ftn.siit.project.isspoject.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
//extends JpaRepository<Reaction, Integer>
public interface ReactionRepository {
    List<Reaction> findByStatus(Status status);
}
