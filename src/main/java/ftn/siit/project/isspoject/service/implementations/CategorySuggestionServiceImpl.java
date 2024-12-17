package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.category.NewCategorySuggestionDTO;
import ftn.siit.project.isspoject.entity.CategorySuggestion;
import ftn.siit.project.isspoject.entity.Status;
import ftn.siit.project.isspoject.repository.CategorySuggestionRepository;
import ftn.siit.project.isspoject.service.interfaces.CategorySuggestionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return categorySuggestionRepository.findById(id);
    }

    @Override
    public CategorySuggestion save(CategorySuggestion categorySuggestion) {
        return categorySuggestionRepository.save(categorySuggestion);
    }

    @Override
    public CategorySuggestion save(NewCategorySuggestionDTO dto) {
        CategorySuggestion categorySuggestion = new CategorySuggestion();
        categorySuggestion.setSuggestion(dto.getSuggestion());
        categorySuggestion.setStatus(Status.valueOf(dto.getStatus()));
        return categorySuggestionRepository.save(categorySuggestion);
    }

    @Override
    public CategorySuggestion update(CategorySuggestion categorySuggestion) {
        CategorySuggestion existingCategorySuggestion = findById(categorySuggestion.getId());
        if (existingCategorySuggestion == null) {
            throw new EntityNotFoundException("Category suggestion does not exist");
        }
        existingCategorySuggestion.setSuggestion(categorySuggestion.getSuggestion());
        existingCategorySuggestion.setStatus(categorySuggestion.getStatus());
        return categorySuggestionRepository.save(existingCategorySuggestion);
    }

    @Override
    public void delete(CategorySuggestion categorySuggestion) {
        categorySuggestionRepository.delete(categorySuggestion);
    }
}
