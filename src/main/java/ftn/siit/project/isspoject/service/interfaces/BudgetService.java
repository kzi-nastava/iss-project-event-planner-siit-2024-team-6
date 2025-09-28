package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.entity.Category;

import java.util.Optional;

public interface BudgetService {
    Budget save(Budget budget);
    Budget addNewItem(Category category, double price, int budgetId);
    Budget removeItem(int id, Category category);
    BudgetItem addItemToBudget(int budgetId, String category, double price);
    BudgetItem updateBudgetItem(int budgetId, int itemId, double price);
    void removeBudgetItem(int budgetId, int itemId);
}
