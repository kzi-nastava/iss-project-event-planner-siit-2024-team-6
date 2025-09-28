package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.category.NewCategoryDTO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "category_suggestions")
public class CategorySuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;
    String description;
    Status status;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "offer_id", referencedColumnName = "id")
    Offer offer;

    public CategorySuggestion(){}
    public CategorySuggestion(NewCategoryDTO suggestion, Status status, Offer offer) {
        this.name = suggestion.getName();
        this.description = suggestion.getDescription();
        this.status = status;
        this.offer = offer;
    }
}
