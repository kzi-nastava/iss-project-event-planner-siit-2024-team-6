package ftn.siit.project.isspoject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private double total;
    private double available;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_id")
    private List<BudgetItem> budgetItems = new ArrayList<>();

    public Budget() {
    }
}
