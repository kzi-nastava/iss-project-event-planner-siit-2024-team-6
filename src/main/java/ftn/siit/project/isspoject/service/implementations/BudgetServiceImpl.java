package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.BudgetRepository;
import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.service.interfaces.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private CategoryRepository categoryRepository;

  //  public Page<Budget> findAll(Pageable page) {
//        return budgetRepository.findAll(page);
//    }

    @Override
    public Budget findById(Integer id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with ID: " + id));
    }

    @Override
    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Override
    public Budget save(NewBudgetDTO budgetDTO) {
        Budget budget = new Budget();
        budget.setTotal(0);
        double currents = 0;
        for(NewBudgetItemDTO dto:budgetDTO.getBudgetItems()){
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setCategory(categoryRepository.findByNameIgnoreCase(dto.getCategory()));
            budgetItem.setCurrPrice(dto.getCurrPrice());
            budgetItem.setMaxPrice(dto.getMaxPrice());
            budget.setTotal(budget.getTotal()+budgetItem.getMaxPrice());
            currents += budgetItem.getCurrPrice();
            budget.getBudgetItems().add(budgetItem);
        }
        budget.setAvailable(budget.getTotal()-currents);
        return budgetRepository.save(budget);
    }


    @Override
    public Budget update(Budget updatedBudget) {
        Budget existingBudget = budgetRepository.findById(updatedBudget.getId())
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with ID: " + updatedBudget.getId()));

        existingBudget.setTotal(updatedBudget.getTotal());
        existingBudget.setAvailable(updatedBudget.getAvailable());

        if (updatedBudget.getBudgetItems() != null) {
            existingBudget.getBudgetItems().clear();
            existingBudget.getBudgetItems().addAll(updatedBudget.getBudgetItems());
        }

        return budgetRepository.save(existingBudget);
    }

    @Override
    public Budget update(int id, NewBudgetDTO budgetDTO) {
          Budget existingBudget = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with ID: " + id));

          double currents = 0;
          existingBudget.setTotal(0);
          existingBudget.getBudgetItems().clear();
          for(NewBudgetItemDTO dto:budgetDTO.getBudgetItems()){
              BudgetItem budgetItem = new BudgetItem();
              budgetItem.setCategory(categoryRepository.findByNameIgnoreCase(dto.getCategory()));
              budgetItem.setCurrPrice(dto.getCurrPrice());
              budgetItem.setMaxPrice(dto.getMaxPrice());
              existingBudget.getBudgetItems().add(budgetItem);
              existingBudget.setTotal(existingBudget.getTotal()+budgetItem.getMaxPrice());
              currents += budgetItem.getCurrPrice();
          }
          existingBudget.setAvailable(existingBudget.getTotal()-currents);
          return budgetRepository.save(existingBudget);
    }

    @Override
    public void delete(int id) {
        budgetRepository.deleteById(id);
    }
}
