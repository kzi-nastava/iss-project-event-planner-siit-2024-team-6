package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.ClosedEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<Event> findAll();
    //Page<Event> findAll(Pageable page);
    Event findById(Integer eventId);
    List<Event> findByOrganizer(Organizer organizer);

    List<Event> findTopFive();

    Event save(Event event);
    void delete(Event event);
    List<Event> getEventsUserAttends(Integer userId);
    void addClosedEvent(ClosedEventDTO eventDTO);
    List<Event> searchEvents(String name, String description, String place, EventType eventType, Boolean isPublic, LocalDateTime startDate, LocalDateTime endDate);
}
