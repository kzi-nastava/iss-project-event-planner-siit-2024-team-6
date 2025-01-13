package ftn.siit.project.isspoject.dto.category;

import ftn.siit.project.isspoject.entity.CategorySuggestion;
import ftn.siit.project.isspoject.entity.Report;
import ftn.siit.project.isspoject.entity.Status;
import lombok.Data;

@Data
public class CategorySuggestionDTO {
    Integer id;
    String name;
    String description;
    String offerName;
    String offerDescription;

    public CategorySuggestionDTO() {}

    public CategorySuggestionDTO(CategorySuggestion cs) {
        this.id = cs.getId();
        this.name = cs.getName();
        this.description = cs.getDescription();
        this.offerName = cs.getOffer().getName();
        this.offerDescription = cs.getOffer().getDescription();
    }
}
