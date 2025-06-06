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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return eventRepository.findByIsDeletedFalse(page,LocalDateTime.now());
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
    public List<Event> getFutureEvents(Organizer organizer) {
        return eventRepository.findFutureEventsByOrganizer(organizer, LocalDateTime.now());
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

    public Page<Event> searchEvents(String name, String description, String place, String eventType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String sortDir) {
        System.out.println("Search Events called with parameters:");
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Place: " + place);
        System.out.println("Event Type: " + eventType);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Page Number: " + pageable.getPageNumber());
        System.out.println("Page Size: " + pageable.getPageSize());

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        LocalDateTime now = LocalDateTime.now();

        List<Event> events = eventRepository.findAll();

        List<Event> filteredEvents = events.stream()
                .filter(event -> !event.getIsDeleted())
                .filter(event -> event.getDate().isAfter(now))
                .filter(event ->
                        (name == null || name.isEmpty() || event.getName().toLowerCase().contains(name.toLowerCase())) ||
                                (description == null || description.isEmpty() || event.getDescription().toLowerCase().contains(description.toLowerCase())) ||
                                (place == null || place.isEmpty() || event.getPlace().toLowerCase().contains(place.toLowerCase())))
                .filter(event -> eventType == null || eventType.isEmpty() || event.getEventType().getName().equals(eventType))
                .filter(event -> (startDate == null || event.getDate().isAfter(startDate)) &&
                        (endDate == null || event.getDate().isBefore(endDate)))
                .sorted((e1, e2) ->
                        direction == Sort.Direction.ASC
                                ? e1.getDate().compareTo(e2.getDate())
                                : e2.getDate().compareTo(e1.getDate())
                )
                .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredEvents.size());

        List<Event> paginatedEvents = filteredEvents.subList(start, end);

        return new PageImpl<>(paginatedEvents, pageable, filteredEvents.size());

    }
}