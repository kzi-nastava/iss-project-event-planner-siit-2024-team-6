package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.BudgetDTO;
import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.service.BudgetService;
import ftn.siit.project.isspoject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets/")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("{id}")
    public ResponseEntity<BudgetDTO> getBudget(@PathVariable int id) {
        Budget budget = budgetService.findById(id);
        if(budget == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new BudgetDTO(budget));
    }

    @PostMapping("add")
    public ResponseEntity<String> createBudget(@RequestBody BudgetDTO budgetDTO) {
        Budget budget = new Budget(budgetDTO, categoryService.findAllByNames(budgetDTO.getCategories()));
        if(budget == null){ throw new IllegalArgumentException("Budget is null"); }
        budgetService.save(budget);
        return ResponseEntity.ok("Budget created.");
    }

    @PutMapping("update/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(@PathVariable int id, @RequestBody BudgetDTO budgetDTO) {
        Budget existingBudget = budgetService.findById(id);
        if(existingBudget == null) return ResponseEntity.notFound().build();

        existingBudget.setMaxPrices(budgetDTO.getMaxPrices());
        existingBudget.setCurrentPrices(budgetDTO.getCurrentPrices());
        existingBudget.setCategories(categoryService.findAllByNames(budgetDTO.getCategories()));
        Budget updatedBudget = budgetService.update(existingBudget);

        return ResponseEntity.ok(new BudgetDTO(updatedBudget));
    }
}
