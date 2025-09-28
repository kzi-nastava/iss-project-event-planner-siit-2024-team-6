package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.NewClosedEventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.dto.event.OrganizersEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<Event> findAll();
    Page<Event> findAll(Pageable page);
    Event findById(Integer eventId);
    List<Event> findTopFive();

    Event save(Event event);
    void delete(Event event);
    List<Event> getEventsUserAttends(Integer userId);
    Page<Event> searchEvents(String name, String description, String place, String eventType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String sortDir);
    void sendInvitations(EventDTO dto, List<String> emails);
    List<Event> findByOrganizer(Organizer organizer);
    List<Event> getFutureEvents(Organizer organizer);
    Boolean checkIfEventHasPassed(int budgetId, Organizer organizer);
    Page<Event> findByOrganizer(Pageable pageable, Organizer organizer);
}
