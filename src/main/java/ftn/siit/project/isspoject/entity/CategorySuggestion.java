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
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "offer_id", referencedColumnName = "id")
    Offer offer;

    public CategorySuggestion(){}
    public CategorySuggestion(String suggestion, Status status, Offer offer) {
        this.suggestion = suggestion;
        this.status = status;
        this.offer = offer;
    }
}
