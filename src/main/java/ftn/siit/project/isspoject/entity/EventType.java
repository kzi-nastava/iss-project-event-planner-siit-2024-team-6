package ftn.siit.project.isspoject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
@Entity
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
