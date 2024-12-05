package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "category_suggestions")
public class CategorySuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String suggestion;
    Status status;
}
