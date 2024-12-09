package ftn.siit.project.isspoject.entity;

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
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetItem> budgetItems = new ArrayList<>();

    public Budget() {
    }
}
