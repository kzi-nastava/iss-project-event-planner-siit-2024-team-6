package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.service.interfaces.BudgetService;
import ftn.siit.project.isspoject.service.interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        System.out.println(budget);
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
    //    @GetMapping(value = "/all_elements")
//    public ResponseEntity<PagedResponse<BudgetDTO>> getBudgetPageAllElements(Pageable page) {
//
//        Page<Budget> budgetsPage = budgetService.findAll(page);
//
//        List<BudgetDTO> budgetDTOs = budgetsPage.stream()
//                .map(BudgetDTO::new)
//                .toList();
//
//        PagedResponse<BudgetDTO> response = new PagedResponse<>(
//                budgetDTOs,
//                budgetsPage.getTotalPages(),
//                budgetsPage.getTotalElements()
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
