package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class BudgetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double maxPrice;
    private double currPrice;
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
