package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.event.NewClosedEventDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.repository.EventTypeRepository;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    private EventTypeRepository eventTypeRepository;

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event findById(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
    }

    @Override
    public List<Event> findByOrganizer(Organizer organizer) {
        return eventRepository.findByOrganizer(organizer);
    }

    @Override
    public List<Event> findTopFive() {
        return eventRepository.findTop5ByOrderByDateAsc();
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void delete(Event event) {
        eventRepository.delete(event);
    }

    @Override
    public List<Event> getEventsUserAttends(Integer userId) {
        return eventRepository.findEventsByUserId(userId);
    }

    public Event addClosedEvent(NewClosedEventDTO eventDTO) {
        EventType type = eventTypeRepository.findById(eventDTO.getEventTypeId())
                .orElseThrow(() -> new RuntimeException("Event type not found with ID: " + eventDTO.getEventTypeId()));
        Event event = new Event(eventDTO);
        event.setEventType(type);
        Event savedEvent = eventRepository.save(event);
        sendInvitations("You are invited to a new event!", eventDTO.getEmails());
        return savedEvent;
    }

    private void sendInvitations(String text, List<String> emails) {}

//    public Page<Event> findAll(Pageable page) {
//        return eventRepository.findAll(page);
//    }

    public List<Event> searchEvents(String name, String description, String place, EventType eventType, Boolean isPublic, LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.searchEvents(name, description, place, eventType, isPublic, startDate, endDate);
    }
}
