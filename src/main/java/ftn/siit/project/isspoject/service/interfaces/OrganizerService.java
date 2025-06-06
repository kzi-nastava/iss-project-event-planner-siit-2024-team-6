package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Organizer;
import org.springframework.data.repository.query.Param;

public interface OrganizerService {

    Organizer findById(Integer organizerId);
    Organizer findOrganizerByEventId(Integer eventId);
    Organizer findByEventId(Integer eventId);
    Organizer findByEmail(String email);
}
