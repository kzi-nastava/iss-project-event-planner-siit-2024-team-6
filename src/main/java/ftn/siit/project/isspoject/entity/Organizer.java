package ftn.siit.project.isspoject.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@DiscriminatorValue("Organizer")
@EqualsAndHashCode(callSuper = true)
@Data
public class Organizer extends User {

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<Event> myEvents;

    public boolean hasFutureEvents() {
        return false;
    }
}

