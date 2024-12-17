package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

        @Autowired
        private CategoryRepository categoryRepository;
//    private final List<Category> categories = List.of(
//            new Category(1, "Electronics", "Devices and gadgets"),
//            new Category(2, "Furniture", "Home and office furniture"),
//            new Category(3, "Groceries", "Everyday essentials"),
//            new Category(4, "Sports", "Sports gear and accessories"),
//            new Category(5, "Beauty", "Cosmetics and skincare"),
//            new Category(6, "Books", "Educational and leisure reading"),
//            new Category(7, "Clothing", "Apparel and accessories"),
//            new Category(8, "Travel", "Transportation and lodging"),
//            new Category(9, "Entertainment", "Movies, music, and games")
//    );

    //public Page<Category> findAll(Pageable page) {
//        return categoryRepository.findAll(page);
//    }

    @Override
    public Category findById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }

    @Override
    public Category findByName(String name) {
        Category category = categoryRepository.findByNameIgnoreCase(name);
        if (category == null) {
            throw new NotFoundException("Category not found with name: " + name);
        }
        return category;
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new NotFoundException("No categories found.");
        }
        return categories;
    }

    @Override
    public List<String> findAllNames() {
        return categoryRepository.findAllNames();
    }

    @Override
    public List<Category> findAllByNames(List<String> names) {
        List<Category> categories = categoryRepository.findAll().stream()
                .filter(category -> names.contains(category.getName()))
                .toList();

        if (categories.isEmpty()) {
            throw new NotFoundException("No categories found matching the given names.");
        }
        return categories;
    }

    @Override
    public Category save(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null while saving.");
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Category category) {
        if (category == null || category.getId() == null) {
            throw new IllegalArgumentException("Category or Category ID cannot be null while updating.");
        }

        Category existingCategory = findById(category.getId());
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void createPendingCategory(String category) {
        throw new UnsupportedOperationException("Creating pending categories is not supported in this implementation.");
    }

    @Override
    public void delete(Category category) {
        throw new UnsupportedOperationException("Deleting categories is not supported in this implementation.");
    }
}
