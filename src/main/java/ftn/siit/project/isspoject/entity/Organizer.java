package ftn.siit.project.isspoject.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Organizer extends User{
    private List<Event> myEvents;
}
