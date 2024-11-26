package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Category;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Override
    public Category findById(Integer id) {
        return null;
    }

    @Override
    public Category findByName(String name) {
        return null;
    }

    @Override
    public List<Category> findAll() {
        return List.of();
    }

    @Override
    public List<Category> findAllByNames(List<String> names) {
        return List.of();
    }

    @Override
    public Category save(Category category) {
        return null;
    }

    @Override
    public Category update(Category category) {
        return null;
    }

    @Override
    public void createPendingCategory(String category) {}
    @Override
    public void delete(Category category) {
    }
}
