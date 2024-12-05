package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.BudgetItem;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.BudgetItemRepository;
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
    private BudgetItemRepository budgetItemRepository;
    @Autowired
    private CategoryRepository categoryRepository;

  //  public Page<Budget> findAll(Pageable page) {
//        return budgetRepository.findAll(page);
//    }

    @Override
    public Budget findById(Integer id) {
        return null;
    }

    @Override
    public Budget save(Budget budget) {
        Budget b = budgetRepository.save(budget);
        return b;
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
            budgetItemRepository.save(budgetItem);
            budget.getBudgetItems().add(budgetItem);
        }
        budget.setLeft(budget.getTotal()-currents);
        return budgetRepository.save(budget);
    }

    @Override
    public Budget update(Budget budget) {
        Budget existingBudget = findById(budget.getId());
        if (existingBudget != null) {
            throw new NotFoundException("Budget does not exist, cannot be updated");
        }
        existingBudget.setTotal(0);
        double currents = 0;
        existingBudget.getBudgetItems().clear();
        for(BudgetItem item:budget.getBudgetItems()){
            existingBudget.getBudgetItems().add(item);
            existingBudget.setTotal(budget.getTotal()+item.getMaxPrice());
            currents += item.getCurrPrice();
        }
        existingBudget.setLeft(existingBudget.getTotal()-currents);
        return budgetRepository.save(existingBudget);
    }

    @Override
    public Budget update(int id, NewBudgetDTO budgetDTO) {
        Budget budget = findById(id);
        budget.setTotal(0);
        double currents = 0;
        for(NewBudgetItemDTO dto:budgetDTO.getBudgetItems()){
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setCategory(categoryRepository.findByNameIgnoreCase(dto.getCategory()));
            budgetItem.setCurrPrice(dto.getCurrPrice());
            budgetItem.setMaxPrice(dto.getMaxPrice());
            budget.setTotal(budget.getTotal()+budgetItem.getMaxPrice());
            currents += budgetItem.getCurrPrice();
            budgetItemRepository.save(budgetItem);
            budget.getBudgetItems().add(budgetItem);
        }
        budget.setLeft(budget.getTotal()-currents);
        return save(budget);
    }

    @Override
    public void delete(int id) {
        budgetRepository.deleteById(id);
    }

}
