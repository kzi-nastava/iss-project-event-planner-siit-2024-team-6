package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private Product product;

    public Purchase() {
    }
    public Purchase(Organizer organizer, Product product) {
        this.organizer = organizer;
        this.product = product;
    }
}
