package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class BudgetDTO {
    private Integer id;
    private List<Double> maxPrices;
    private List<Double> currentPrices;
    private List<String> categories;

    public BudgetDTO() {}

    public BudgetDTO(Budget budget){
        this.id = budget.getId();
        this.maxPrices = budget.getMaxPrices();
        this.currentPrices = budget.getCurrentPrices();
        this.categories = new ArrayList<>();
        for (Category category : budget.getCategories()) {
            this.categories.add(category.getName());
        }
    }
}
