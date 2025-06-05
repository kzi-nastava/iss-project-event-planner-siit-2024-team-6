package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.Category;

import java.util.Optional;

public interface BudgetService {
    Budget findById(Integer id);
    Budget save(Budget budget);
    Budget save(NewBudgetDTO budgetDTO);
    Budget update(Budget budget);
    Budget update(int id, NewBudgetDTO budgetDTO);
    void delete(int id);
    Budget addNewItem(Category category, double price, int budgetId);
    Budget removeItem(int id, Category category);
}
