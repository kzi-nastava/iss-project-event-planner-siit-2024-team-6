package ftn.siit.project.isspoject.dto.budget;

import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.entity.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class BudgetDTO {
    private Integer id;
    private double total;
    private double left;
    private List<BudgetItemDTO> budgetItems;

    public BudgetDTO() {}

    public BudgetDTO(Budget budget) {
        this.id = budget.getId();
        this.total = budget.getTotal();
        this.left = budget.getLeft();
        this.budgetItems = new ArrayList<>();
        for(BudgetItem budgetItem : budget.getBudgetItems()) {
            this.budgetItems.add(new BudgetItemDTO(budgetItem));
        }
    }
}
