package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final List<Category> categories = List.of(
            new Category(1, "Electronics", "Devices and gadgets"),
            new Category(2, "Furniture", "Home and office furniture"),
            new Category(3, "Groceries", "Everyday essentials"),
            new Category(4, "Sports", "Sports gear and accessories"),
            new Category(5, "Beauty", "Cosmetics and skincare"),
            new Category(6, "Books", "Educational and leisure reading"),
            new Category(7, "Clothing", "Apparel and accessories"),
            new Category(8, "Travel", "Transportation and lodging"),
            new Category(9, "Entertainment", "Movies, music, and games")
    );

    //public Page<Category> findAll(Pageable page) {
//        return categoryRepository.findAll(page);
//    }

    @Override
    public Category findById(Integer id) {
        return categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Category findByName(String name) {
        return categories.stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Category> findAll() {
        return categories;
    }

    @Override
    public List<Category> findAllByNames(List<String> names) {
        return categories.stream()
                .filter(category -> names.contains(category.getName()))
                .toList();
    }

    @Override
    public Category save(Category category) {
        categories.add(category);
        return category;
    }

    @Override
    public Category update(Category category) {
        Category existingCategory = findById(category.getId());
        if (existingCategory != null) {
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            return existingCategory;
        }
        return null;
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
