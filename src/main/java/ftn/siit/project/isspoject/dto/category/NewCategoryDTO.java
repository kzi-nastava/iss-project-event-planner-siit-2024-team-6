package ftn.siit.project.isspoject.dto.category;

import ftn.siit.project.isspoject.entity.Category;
import lombok.Data;

@Data
public class NewCategoryDTO {
    private String name;
    private String description;
    public NewCategoryDTO(){}
    public NewCategoryDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public NewCategoryDTO(Category category) {
        this.name = category.getName();
        this.description = category.getDescription();
    }
}
