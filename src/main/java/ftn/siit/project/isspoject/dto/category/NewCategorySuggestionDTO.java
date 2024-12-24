package ftn.siit.project.isspoject.dto.category;

import ftn.siit.project.isspoject.entity.CategorySuggestion;
import lombok.Data;

@Data
public class NewCategorySuggestionDTO {
    String suggestion;

    NewCategorySuggestionDTO(){}
    NewCategorySuggestionDTO(CategorySuggestion categorySuggestion) {
        this.suggestion = categorySuggestion.getSuggestion();
    }
}
