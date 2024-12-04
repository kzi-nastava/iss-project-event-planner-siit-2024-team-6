package ftn.siit.project.isspoject.entity;
import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class OfferHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToMany(mappedBy = "offer_history")
    private List<Offer> offers;
    @ElementCollection
    @CollectionTable(name = "offer_history_timestamps", joinColumns = @JoinColumn(name = "offer_history_id"))
    @Column(name = "timestamps")
    private List<LocalDateTime> timestamps;
}
