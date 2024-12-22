package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Organizer;

public interface OrganizerService {

    Organizer findById(Integer organizerId);
    Organizer findByEventId(Integer eventId);
}
