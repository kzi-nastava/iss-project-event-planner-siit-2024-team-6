package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByNameIgnoreCase(String name);
    Category findById(int id);
    @Query("SELECT c.name FROM Category c")
    List<String> findAllNames();
}
