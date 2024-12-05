package ftn.siit.project.isspoject.entity;

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
    public EventType (){}
    public EventType(int i, String corporate, String professionalCorporateEvents, boolean b) {
        this.id = i;
        this.name = corporate;
        this.description = professionalCorporateEvents;
        this.isDeleted = b;
    }
}
