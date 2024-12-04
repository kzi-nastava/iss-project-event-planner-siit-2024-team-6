package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.category.NewCategorySuggestionDTO;
import ftn.siit.project.isspoject.entity.CategorySuggestion;

import java.util.List;

public interface CategorySuggestionService {
    List<CategorySuggestion> getPending();
    CategorySuggestion findById(int id);
    CategorySuggestion create(CategorySuggestion categorySuggestion);
    CategorySuggestion save(NewCategorySuggestionDTO dto);
    CategorySuggestion update(CategorySuggestion categorySuggestion);
    void delete(CategorySuggestion categorySuggestion);
}
