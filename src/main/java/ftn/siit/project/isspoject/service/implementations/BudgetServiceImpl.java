package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.service.interfaces.BudgetService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final List<Budget> budgets = new ArrayList<>();

    public BudgetServiceImpl() {
        // Initialize hardcoded budgets
        budgets.add(new Budget(
                1,
                Arrays.asList(100.0, 200.0, 300.0),
                Arrays.asList(90.0, 180.0, 250.0),
                Arrays.asList(
                        new Category(1, "Electronics", "Devices and gadgets"),
                        new Category(2, "Furniture", "Home and office furniture"),
                        new Category(3, "Groceries", "Everyday essentials")
                )
        ));
        budgets.add(new Budget(
                2,
                Arrays.asList(150.0, 250.0),
                new ArrayList<>(), // Empty currentPrices
                Arrays.asList(
                        new Category(1, "Clothing", "Apparel and accessories"),
                        new Category(2, "Accessories", "Bags, jewelry, and more")
                )
        ));
        budgets.add(new Budget(
                3,
                List.of(50.0),
                List.of(45.0),
                List.of(new Category(1, "Books", "Educational and leisure reading"))
        ));
        budgets.add(new Budget(
                4,
                Arrays.asList(200.0, 300.0),
                Arrays.asList(190.0, 280.0),
                new ArrayList<>() // No categories
        ));
        budgets.add(new Budget(
                5,
                Arrays.asList(100.0, 200.0, 300.0, 400.0, 500.0),
                Arrays.asList(95.0, 185.0, 290.0, 380.0, 480.0),
                Arrays.asList(
                        new Category(1, "Electronics", "Devices and gadgets"),
                        new Category(2, "Furniture", "Home and office furniture"),
                        new Category(3, "Groceries", "Everyday essentials"),
                        new Category(4, "Sports", "Sports gear and accessories"),
                        new Category(5, "Beauty", "Cosmetics and skincare")
                )
        ));
        budgets.add(new Budget(
                6,
                Arrays.asList(0.0, 0.0, 0.0),
                Arrays.asList(0.0, 0.0, 0.0),
                Arrays.asList(
                        new Category(1, "Miscellaneous", "Other uncategorized items"),
                        new Category(2, "Entertainment", "Movies, music, and games"),
                        new Category(3, "Travel", "Transportation and lodging")
                )
        ));
    }

  //  public Page<Budget> findAll(Pageable page) {
//        return budgetRepository.findAll(page);
//    }

    @Override
    public Budget findById(Integer id) {
        return budgets.stream()
                .filter(budget -> budget.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Budget save(Budget budget) {
        budgets.add(budget);
        return budget;
    }

    @Override
    public Budget update(Budget budget) {
        Budget existingBudget = findById(budget.getId());
        if (existingBudget != null) {
            budgets.remove(existingBudget);
            budgets.add(budget);
            return budget;
        }
        return null;
    }

    @Override
    public void delete(Budget budget) {
        budgets.remove(budget);
    }
}
