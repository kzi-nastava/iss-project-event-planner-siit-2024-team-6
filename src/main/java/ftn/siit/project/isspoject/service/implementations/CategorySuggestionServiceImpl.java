package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.category.NewCategoryDTO;
import ftn.siit.project.isspoject.entity.CategorySuggestion;
import ftn.siit.project.isspoject.entity.Status;
import ftn.siit.project.isspoject.repository.CategorySuggestionRepository;
import ftn.siit.project.isspoject.service.interfaces.CategorySuggestionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorySuggestionServiceImpl implements CategorySuggestionService {
    @Autowired
    private CategorySuggestionRepository categorySuggestionRepository;

    @Override
    public List<CategorySuggestion> getPending() {
        return categorySuggestionRepository.findByStatus(Status.PENDING);
    }

    @Override
    public Page<CategorySuggestion> getPending(Pageable pageable) {
        return categorySuggestionRepository.findByStatus(Status.PENDING, pageable);
    }

    @Override
    public CategorySuggestion findById(int id) {
        CategorySuggestion cs = categorySuggestionRepository.findById(id);
        if (cs == null) throw new EntityNotFoundException("Category suggestion with id " + id + " not found");
        return cs;
    }

    @Override
    public CategorySuggestion save(CategorySuggestion categorySuggestion) {
        return categorySuggestionRepository.save(categorySuggestion);
    }

    @Override
    public CategorySuggestion update(CategorySuggestion categorySuggestion) {
        CategorySuggestion existingCategorySuggestion = findById(categorySuggestion.getId());
        existingCategorySuggestion.setName(categorySuggestion.getName());
        existingCategorySuggestion.setDescription(categorySuggestion.getDescription());
        existingCategorySuggestion.setStatus(categorySuggestion.getStatus());
        return categorySuggestionRepository.save(existingCategorySuggestion);
    }

    @Override
    public CategorySuggestion update(int id, NewCategoryDTO dto) {
        CategorySuggestion existingCategorySuggestion = findById(id);
        existingCategorySuggestion.setName(dto.getName());
        existingCategorySuggestion.setDescription(dto.getDescription());
        existingCategorySuggestion.setStatus(Status.ACCEPTED);
        return categorySuggestionRepository.save(existingCategorySuggestion);
    }


    @Override
    public CategorySuggestion approve(int id) {
        CategorySuggestion existingCategorySuggestion = findById(id);
        existingCategorySuggestion.setStatus(Status.ACCEPTED);
        return update(existingCategorySuggestion);
    }

    @Override
    public CategorySuggestion reject(int id) {
        CategorySuggestion existingCategorySuggestion = findById(id);
        existingCategorySuggestion.setStatus(Status.REJECTED);
        return categorySuggestionRepository.save(existingCategorySuggestion);
    }
}
