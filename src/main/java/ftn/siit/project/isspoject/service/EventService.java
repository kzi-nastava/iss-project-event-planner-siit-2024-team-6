package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;

import java.util.List;

public interface EventService {
    void save(Event event);

    List<Event> findByOrganizer(Organizer organizer);

    Event findById(Integer eventId);

    void delete(Event event);
}
