package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.entity.Budget;
import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.repository.BudgetItemRepository;
import ftn.siit.project.isspoject.repository.BudgetRepository;
import ftn.siit.project.isspoject.service.interfaces.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetItemRepository budgetItemRepository;

  //  public Page<Budget> findAll(Pageable page) {
//        return budgetRepository.findAll(page);
//    }

    @Override
    public Budget findById(Integer id) {
        return null;
    }

    @Override
    public Budget save(Budget budget) {
        return budget;
    }

    @Override
    public Budget save(NewBudgetDTO budgetDTO) {
        return null;
    }

    @Override
    public Budget update(Budget budget) {
        Budget existingBudget = findById(budget.getId());
        if (existingBudget != null) {
            return budget;
        }
        return null;
    }

    @Override
    public Budget update(int id, NewBudgetDTO budgetDTO) {
        return null;
    }

    @Override
    public void delete(int id) {

    }

}
