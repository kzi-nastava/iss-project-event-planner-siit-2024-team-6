package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.event.NewClosedEventDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.repository.EventTypeRepository;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        List<Event> events = eventRepository.findAll();
        if (events.isEmpty()) {
            throw new NotFoundException("No events found.");
        }
        return events;
    }

    @Override
    public Page<Event> findAll(Pageable page) {
        return eventRepository.findAll(page);
    }


    @Override
    public Event findById(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with ID: " + eventId));
    }

    @Override
    public List<Event> findByOrganizer(Organizer organizer) {
        List<Event> events = eventRepository.findByOrganizer(organizer);
        if (events.isEmpty()) {
            throw new NotFoundException("No events found for organizer: " + organizer.getName());
        }
        return events;
    }

    @Override
    public List<Event> findTopFive() {
        List<Event> events = eventRepository.findTop5ByOrderByDateAsc();
        if (events.isEmpty()) {
            throw new NotFoundException("No top 5 upcoming events found.");
        }
        return events;
    }

    @Override
    public Event save(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null while saving.");
        }
        return eventRepository.save(event);
    }

    @Override
    public void delete(Event event) {
        if (event == null || !eventRepository.existsById(event.getId())) {
            throw new NotFoundException("Event not found or already deleted with ID: " + (event != null ? event.getId() : "null"));
        }
        eventRepository.delete(event);
    }

    @Override
    public List<Event> getEventsUserAttends(Integer userId) {
        List<Event> events = eventRepository.findEventsByUserId(userId);
        if (events.isEmpty()) {
            throw new NotFoundException("No events found for user with ID: " + userId);
        }
        return events;
    }

    public Event addClosedEvent(NewClosedEventDTO eventDTO) {
        EventType type = eventTypeRepository.findById(eventDTO.getEventTypeId())
                .orElseThrow(() -> new NotFoundException("Event type not found with ID: " + eventDTO.getEventTypeId()+ "while creating closed event"));

        Event event = new Event(eventDTO);
        event.setEventType(type);
        Event savedEvent = eventRepository.save(event);
        sendInvitations("You are invited to a new event!", eventDTO.getEmails());
        return savedEvent;
    }

    private void sendInvitations(String text, List<String> emails) {
    }

    public List<Event> searchEvents(String name, String description, String place, EventType eventType, Boolean isPublic, LocalDateTime startDate, LocalDateTime endDate) {
        List<Event> events = eventRepository.searchEvents(name, description, place, eventType, isPublic, startDate, endDate);
        if (events.isEmpty()) {
            throw new NotFoundException("No events found matching the given criteria.");
        }
        return events;
    }
}
