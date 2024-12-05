package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.category.NewCategorySuggestionDTO;
import ftn.siit.project.isspoject.entity.CategorySuggestion;
import ftn.siit.project.isspoject.repository.CategorySuggestionRepository;
import ftn.siit.project.isspoject.service.interfaces.CategorySuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorySuggestionServiceImpl implements CategorySuggestionService {
    @Autowired
    private CategorySuggestionRepository categorySuggestionRepository;

    @Override
    public List<CategorySuggestion> getPending() {
        return List.of();
    }

    @Override
    public CategorySuggestion findById(int id) {
        return null;
    }

    @Override
    public CategorySuggestion create(CategorySuggestion categorySuggestion) {
        return null;
    }

    @Override
    public CategorySuggestion save(NewCategorySuggestionDTO dto) {
        return null;
    }

    @Override
    public CategorySuggestion update(CategorySuggestion categorySuggestion) {
        return null;
    }

    @Override
    public void delete(CategorySuggestion categorySuggestion) {

    }
}
