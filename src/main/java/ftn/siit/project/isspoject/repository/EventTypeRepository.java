package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
     Optional<EventType> findByNameAndIsDeletedFalse(String name);
     @Query("SELECT c.name FROM EventType c WHERE c.isDeleted IS FALSE")
     List<String> findAllNames();

     @Query("SELECT e FROM EventType e JOIN e.categories c WHERE c.id = :categoryId AND e.isDeleted = false")
     List<EventType> findByCategoryId(@Param("categoryId") Integer categoryId);
}
