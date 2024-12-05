package ftn.siit.project.isspoject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@DiscriminatorValue("Organizer")
@EqualsAndHashCode(callSuper = true)
@Data
public class Organizer extends User {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "organizer_id")
    private List<Event> myEvents;

    public boolean hasFutureEvents() {
        return false;
    }
}

