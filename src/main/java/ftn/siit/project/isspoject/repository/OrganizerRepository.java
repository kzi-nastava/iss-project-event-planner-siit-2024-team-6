package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Integer> {

}
