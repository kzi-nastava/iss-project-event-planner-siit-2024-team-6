package ftn.siit.project.isspoject.dto.category;

import ftn.siit.project.isspoject.entity.CategorySuggestion;
import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.entity.Status;

public class CategorySuggestionDTO {
    Integer id;
    String suggestion;
    String status;

    public CategorySuggestionDTO() {}

    public CategorySuggestionDTO(CategorySuggestion cs) {
        this.id = cs.getId();
        this.suggestion = cs.getSuggestion();
        this.status = cs.getStatus().toString();
    }
}
