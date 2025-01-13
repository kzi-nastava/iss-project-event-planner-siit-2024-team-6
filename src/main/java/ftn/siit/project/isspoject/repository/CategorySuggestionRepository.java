package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.CategorySuggestion;
import ftn.siit.project.isspoject.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorySuggestionRepository extends JpaRepository<CategorySuggestion, Integer>{
    CategorySuggestion findById(int id);
    Page<CategorySuggestion> findByStatus(Status s, Pageable pageable);
    List<CategorySuggestion> findByStatus(Status s);
}
