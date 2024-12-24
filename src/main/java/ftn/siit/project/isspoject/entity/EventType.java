package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import jakarta.persistence.*;
import ftn.siit.project.isspoject.dto.event.NewEventTypeDTO;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "event_types")
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Boolean isDeleted;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "eventtype_categories",
            joinColumns = @JoinColumn(name = "eventtype_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
    public EventType (){}
    public EventType(int i, String corporate, String professionalCorporateEvents, boolean b) {
        this.id = i;
        this.name = corporate;
        this.description = professionalCorporateEvents;
        this.isDeleted = b;
    }

    public EventType(EventTypeDTO eventType) {

    }
}
