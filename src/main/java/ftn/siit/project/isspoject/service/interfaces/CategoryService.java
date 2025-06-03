package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.category.NewCategoryDTO;
import ftn.siit.project.isspoject.dto.pagination.PagedResponse;
import ftn.siit.project.isspoject.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Category findById(Integer id);
    Category findByName(String name);
    Page<Category> findAll(Pageable pageable);
    List<Category> findAll();
    List<String> findAllNames();
    List<Category> findAllByNames(List<String> names);
    void createPendingCategory(String category);
    Category save(String name, String description);
    Category save(NewCategoryDTO newCategoryDTO);
    Category save(Category category);
    Category update(int id, NewCategoryDTO newCategoryDTO);
    Category update(Category category);
    void delete(Category category);
    void delete(Integer id);
}
