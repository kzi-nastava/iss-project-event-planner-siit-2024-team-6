package ftn.siit.project.isspoject.service.implementations;

import java.time.LocalDateTime;

import ftn.siit.project.isspoject.dto.EmailDetails;
import ftn.siit.project.isspoject.dto.event.NewClosedEventDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.repository.EventTypeRepository;
import ftn.siit.project.isspoject.service.external.EmailService;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventTypeRepository eventTypeRepository;
    @Autowired
    private EmailService emailService;

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
        //Event savedEvent = eventRepository.save(event);
        String invitation = generateInvitation(eventDTO);
        sendInvitations(invitation, eventDTO.getEmails());
        return event;
    }

    public String generateInvitation(NewClosedEventDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a");

        return String.format(
                "🌟 You Are Invited to \"%s\" 🌟\n\n" +
                        "%s\n\n" +
                        "📍 **Location**: %s\n" +
                        "📅 **Date and Time**: %s\n\n" +
                        "We would be delighted by your presence!\n\n" +
                        "Please mark your calendar and join us for this special event.",
                dto.getName() != null ? dto.getName() : "Untitled Event",
                dto.getDescription() != null ? dto.getDescription() : "No description available.",
                dto.getPlace() != null ? dto.getPlace() : "Location not specified",
                dto.getDate() != null ? dto.getDate().format(formatter) : "Date not specified"
        );
    }
    private void sendInvitations(String text, List<String> emails) {
        for (String email : emails) {
            EmailDetails details = new EmailDetails();
            details.setRecipient(email);
            details.setSubject("You're Invited!");
            details.setMsgBody(text);

            String status = emailService.sendSimpleMail(details);
            System.out.println("Invitation sent to: " + email + " - Status: " + status);
        }
    }

    public List<Event> searchEvents(String name, String description, String place, EventType eventType, Boolean isPublic, LocalDateTime startDate, LocalDateTime endDate) {
        List<Event> events = eventRepository.searchEvents(name, description, place, eventType, isPublic, startDate, endDate);
        if (events.isEmpty()) {
            throw new NotFoundException("No events found matching the given criteria.");
        }
        return events;
    }
}
