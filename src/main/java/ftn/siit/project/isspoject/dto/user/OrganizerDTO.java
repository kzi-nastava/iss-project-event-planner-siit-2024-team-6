package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganizerDTO extends UserDTO {
    private List<EventDTO> myEvents;

    public OrganizerDTO() {
        super();
    }

    public OrganizerDTO(Organizer savedUser) {
        super(savedUser);
        if (savedUser != null) {
            this.myEvents = new ArrayList<>();
            if (savedUser.getMyEvents() != null) {
                for (Event event : savedUser.getMyEvents()) {
                    this.myEvents.add(new EventDTO(event));
                }
            }
        }
    }
}
