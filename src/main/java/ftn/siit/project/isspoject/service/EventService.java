package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;

import java.util.List;

public interface EventService {

    List<Event> findAll();
    Event findById(Integer eventId);
    List<Event> findByOrganizer(Organizer organizer);

    List<Event> findTopFive();

    Event save(Event event);
    void delete(Event event);

}
