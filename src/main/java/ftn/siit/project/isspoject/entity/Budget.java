package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Budget {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;
     @OneToMany(cascade = CascadeType.ALL)
     @JoinColumn(name = "budget_item_id", nullable = false)
     private List<BudgetItem> budgetItems;

     public Budget() {}
}
