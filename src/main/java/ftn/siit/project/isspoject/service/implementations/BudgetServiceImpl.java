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
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.jdbc.Expectation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private CategoryRepository categoryRepository;

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
    public BudgetItem addItemToBudget(int budgetId, String category, double price) {
        Budget b = findById(budgetId);

        for (BudgetItem bi : b.getBudgetItems()) {
            if (bi.getCategory().getName().equalsIgnoreCase(category)) {
                throw new IllegalArgumentException("Category already exists in budget");
            }
        }

        Category c = categoryRepository.findByNameIgnoreCaseAndIsDeletedIsFalse(category);
        if (c == null) {
            throw new IllegalArgumentException("Category not found");
        }

        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setCategory(c);
        budgetItem.setMaxPrice(price);
        budgetItem.setCurrPrice(0.0);
        b.getBudgetItems().add(budgetItem);

        Budget updated = update(b);
        for (BudgetItem bi : updated.getBudgetItems()) {
            if (bi.getCategory().getName().equalsIgnoreCase(category)) {
                return bi;
            }
        }

        throw new NotFoundException("Budget item not found after creation");
    }

    @Override
    public BudgetItem updateBudgetItem(int budgetId, int itemId, double price) {
        if (price <= 0){
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        Budget b = findById(budgetId);
        boolean found = false;
        for(BudgetItem bi : b.getBudgetItems()) {
            if(bi.getId() == itemId){
                if(bi.getCurrPrice() > price){
                    throw new IllegalArgumentException("Spent amount cannot be greater than budget item amount");
                }else{
                    bi.setMaxPrice(price);
                    found = true;
                    break;
                }
            }
        }
        if(!found){
            throw new IllegalArgumentException("Budget item not found");
        }
        Budget updated = update(b);
        for(BudgetItem bi : updated.getBudgetItems()) {
             if(bi.getId() == itemId){
                 return bi;
             }
         }
        throw new NotFoundException("Budget item not found after update");
    }

    @Override
    public void removeBudgetItem(int budgetId, int itemId) {
        Budget b = findById(budgetId);

        Iterator<BudgetItem> iterator = b.getBudgetItems().iterator();
        while (iterator.hasNext()) {
            BudgetItem bi = iterator.next();
            if (bi.getId() == itemId) {
                if (bi.getCurrPrice() > 0.0) {
                    throw new IllegalArgumentException("Budget item cannot be deleted. Offers already purchased for this item.");
                } else {
                    iterator.remove();
                    update(b);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Budget item not found");
    }



    @Override
    public Budget addNewItem(Category category, double price, int budgetId){
        Budget b = findById(budgetId);
        for(BudgetItem bi : b.getBudgetItems()) {
            if(bi.getCategory().equals(category)){
                bi.setCurrPrice(bi.getCurrPrice() + price);
                return update(b);
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

    private Budget findById(Integer id) {
         return budgetRepository.findByIdWithItems(id).orElseThrow(() -> new IllegalArgumentException("Budget not found"));
    }

    private Budget update(Budget budget) {
        if (!budgetRepository.findById(budget.getId()).isPresent()) {
            throw new IllegalArgumentException("Budget not found");
        }
        double total = 0.0;
        double spent = 0.0;
        for(BudgetItem bi : budget.getBudgetItems()) {
            total += bi.getMaxPrice();
            spent += bi.getCurrPrice();
        }
        budget.setAvailable(total - spent);
        budget.setTotal(total);
        return budgetRepository.save(budget);
    }

}
