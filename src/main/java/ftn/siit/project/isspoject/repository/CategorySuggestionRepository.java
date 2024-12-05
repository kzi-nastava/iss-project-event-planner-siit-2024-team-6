package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.CategorySuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorySuggestionRepository extends JpaRepository<CategorySuggestion, Integer>{
    CategorySuggestion findById(int id);
}
