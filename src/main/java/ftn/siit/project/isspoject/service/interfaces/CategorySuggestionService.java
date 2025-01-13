package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.category.NewCategoryDTO;
import ftn.siit.project.isspoject.entity.CategorySuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategorySuggestionService {
    List<CategorySuggestion> getPending();
    Page<CategorySuggestion> getPending(Pageable pageable);
    CategorySuggestion findById(int id);
    CategorySuggestion save(CategorySuggestion categorySuggestion);
    CategorySuggestion update(CategorySuggestion categorySuggestion);
    CategorySuggestion update(int id, NewCategoryDTO dto);
    CategorySuggestion approve(int id);
    CategorySuggestion reject(int id);
}
