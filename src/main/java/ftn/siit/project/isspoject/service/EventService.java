package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.ClosedEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.User;

import java.util.List;

public interface EventService {

    List<Event> findAll();
    Event findById(Integer eventId);
    List<Event> findByOrganizer(Organizer organizer);

    List<Event> findTopFive();

    Event save(Event event);
    void delete(Event event);
    List<Event> getEventsUserAttends(Integer userId);
    void addClosedEvent(ClosedEventDTO eventDTO);
}
