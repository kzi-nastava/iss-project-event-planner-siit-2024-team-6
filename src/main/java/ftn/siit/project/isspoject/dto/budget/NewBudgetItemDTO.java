package ftn.siit.project.isspoject.dto.budget;

import lombok.Data;

@Data
public class NewBudgetItemDTO {
    double maxPrice;
    double currPrice;
    String category;
}
