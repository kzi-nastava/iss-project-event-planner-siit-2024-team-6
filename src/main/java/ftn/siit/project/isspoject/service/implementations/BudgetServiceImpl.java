package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.BudgetRepository;
import ftn.siit.project.isspoject.repository.CategoryRepository;
import ftn.siit.project.isspoject.service.interfaces.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Budget removeItem(int id, Category category) {
        Budget b = findById(id);
        for(BudgetItem bi : b.getBudgetItems()) {
            if(bi.getCategory().equals(category)){
                b.getBudgetItems().remove(bi);
                return update(b);
            }
        }
        return b;
    }
    @Override
    public Budget addNewItem(Category category, double price, int budgetId){
        Budget b = findById(budgetId);
        for(BudgetItem bi : b.getBudgetItems()) {
            if(bi.getCategory().equals(category)){
                if(bi.getMaxPrice()- bi.getCurrPrice() >= price){
                    bi.setCurrPrice(bi.getCurrPrice() + price);
                    return update(b);
                }
            }
        }
        BudgetItem i = new BudgetItem();
        i.setCategory(category);
        i.setCurrPrice(price);
        i.setMaxPrice(0);
        b.getBudgetItems().add(i);
        b.setBudgetItems(b.getBudgetItems());
        return update(b);
    }

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
