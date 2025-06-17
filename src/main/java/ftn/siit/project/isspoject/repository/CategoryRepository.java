package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByNameIgnoreCaseAndIsDeletedIsFalse(String name);
    @Query("SELECT c.name FROM Category c WHERE c.isDeleted IS FALSE")
    List<String> findAllNames();
    Page<Category> findAllByIsDeletedIsFalse(Pageable pageable);
}
