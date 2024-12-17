package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Category;
import java.util.List;

public interface CategoryService {
    Category findById(Integer id);
    Category findByName(String name);
    List<Category> findAll();
    List<String> findAllNames();
    List<Category> findAllByNames(List<String> names);
    void createPendingCategory(String category);


    Category save(Category category);
    Category update(Category category);
    void delete(Category category);
}
