package ftn.siit.project.isspoject.dto.budget;

import ftn.siit.project.isspoject.entity.BudgetItem;
import lombok.Data;

@Data
public class BudgetItemDTO {
    private Integer id;
    private double maxPrice;
    private double currPrice;
    private String category;

    public BudgetItemDTO() {}

    public BudgetItemDTO(BudgetItem budgetItem) {
        this.id = budgetItem.getId();
        this.maxPrice = budgetItem.getMaxPrice();
        this.currPrice = budgetItem.getCurrPrice();
        this.category = budgetItem.getCategory().getName();
    }
}
