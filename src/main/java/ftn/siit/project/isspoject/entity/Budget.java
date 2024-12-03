package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Budget {
     private Integer id;
     private List<Double> maxPrices;
     private List<Double> currentPrices;
     private List<Category> categories;

     public Budget() {}

     public Budget (BudgetDTO budgetDTO, List<Category> categories) {
          this.id = budgetDTO.getId();
          this.maxPrices = budgetDTO.getMaxPrices();
          this.currentPrices = budgetDTO.getCurrentPrices();
          this.categories = categories;
     }
     public Budget (Integer id, List<Double> maxPrices, List<Double> currentPrices, List<Category> categories) {
          this.id = id;
          this.maxPrices = maxPrices;
          this.currentPrices = currentPrices;
          this.categories = categories;
     }
}
