package ftn.siit.project.isspoject.dto.user;

import ftn.siit.project.isspoject.entity.Organizer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganizerDTO extends UserDTO{
    public OrganizerDTO(Organizer savedUser) {
    }
}
