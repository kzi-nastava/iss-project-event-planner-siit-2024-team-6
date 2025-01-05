package ftn.siit.project.isspoject.service.implementations;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import ftn.siit.project.isspoject.dto.EmailDetails;
import ftn.siit.project.isspoject.dto.event.NewClosedEventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.EventRepository;
import ftn.siit.project.isspoject.repository.EventTypeRepository;
import ftn.siit.project.isspoject.service.external.EmailService;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventTypeRepository eventTypeRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;

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
//        List<Event> onlyActiveEvents = new ArrayList<>();
//        for(Event e: events){
//            if(!e.getIsDeleted()){
//                onlyActiveEvents.add()
//            }
//        }
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
        event.setIsDeleted(!event.getIsDeleted());
        eventRepository.save(event);
    }

    @Override
    public List<Event> getEventsUserAttends(Integer userId) {
        List<Event> events = eventRepository.findEventsByUserId(userId);
        if (events.isEmpty()) {
            throw new NotFoundException("No events found for user with ID: " + userId);
        }
        return events;
    }

    public String generateInvitation(NewEventDTO dto) {
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
    public void sendInvitations(NewEventDTO dto) {
        String invitation = generateInvitation(dto);
        String userLink;
        for (String email : dto.getEmails()) {
            boolean userExists = userService.existsByEmail(email);
            userLink = generateLink(userExists, email);
            System.out.println(email);
            EmailDetails details = new EmailDetails();
            details.setRecipient(email);
            details.setSubject("You're Invited!");
            details.setMsgBody(invitation+"\n"+userLink);

            String status = emailService.sendSimpleMail(details);
            System.out.println("Invitation sent to: " + email + " - Status: " + status);
        }
    }

    private String generateLink(boolean exists, String email) {
        String baseUrl = "http://localhost:4200";

        if (exists) {
            return baseUrl + "/login?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8) + "&disableEmail=true";
        } else {
            return baseUrl + "/quick-registration?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8) + "&disableEmail=true";
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