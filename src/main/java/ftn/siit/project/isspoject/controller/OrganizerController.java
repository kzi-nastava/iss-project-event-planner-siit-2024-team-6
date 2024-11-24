package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.ActivityDTO;
import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.entity.Activity;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.service.EventService;
import ftn.siit.project.isspoject.service.OrganizerService;
import ftn.siit.project.isspoject.service.PDFGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/organizers")
public class OrganizerController {
    @Autowired
    private EventService eventService;

    @Autowired
    private OrganizerService organizerService;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @PostMapping("events/create")
    public ResponseEntity<String> createEvent(@RequestParam Integer organizerId, @RequestBody EventDTO eventDTO) {
        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
        }

        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setMaxParticipants(eventDTO.getMaxParticipants());
        event.setIsPublic(eventDTO.getIsPublic());
        event.setPlace(eventDTO.getPlace());
        event.setDate(eventDTO.getDate());
        event.setEventType(eventDTO.getEventType());
        event.setParticipants(0);

        event.setOrganizer(organizer);

        eventService.save(event);
        return new ResponseEntity<>("Event created successfully", HttpStatus.CREATED);
    }
    @GetMapping("events/{organizerId}/all")
    public ResponseEntity<List<EventDTO>> getOrganizerEvents(@PathVariable Integer organizerId) {
        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Event> events = eventService.findByOrganizer(organizer);
        List<EventDTO> eventDTOs = events.stream().map(event -> {
            EventDTO dto = new EventDTO();
            dto.setId(event.getId());
            dto.setName(event.getName());
            dto.setDescription(event.getDescription());
            dto.setMaxParticipants(event.getMaxParticipants());
            dto.setIsPublic(event.getIsPublic());
            dto.setPlace(event.getPlace());
            dto.setDate(event.getDate());
            dto.setEventType(event.getEventType());
            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @PutMapping("events/{organizerId}/update/{eventId}")
    public ResponseEntity<String> updateEvent(
            @PathVariable Integer organizerId,
            @PathVariable Integer eventId,
            @RequestBody EventDTO eventDTO) {
        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
        }

        Event event = eventService.findById(eventId);
        if (event == null || !event.getOrganizer().equals(organizer)) {
            return new ResponseEntity<>("Event not found or not owned by this organizer", HttpStatus.NOT_FOUND);
        }

        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setMaxParticipants(eventDTO.getMaxParticipants());
        event.setIsPublic(eventDTO.getIsPublic());
        event.setPlace(eventDTO.getPlace());
        event.setDate(eventDTO.getDate());
        eventService.save(event);

        return new ResponseEntity<>("Event updated successfully", HttpStatus.OK);
    }
    @DeleteMapping("events/{organizerId}/delete/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Integer organizerId, @PathVariable Integer eventId) {
        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
        }

        Event event = eventService.findById(eventId);
        if (event == null || !event.getOrganizer().equals(organizer)) {
            return new ResponseEntity<>("Event not found or not owned by this organizer", HttpStatus.NOT_FOUND);
        }

        eventService.delete(event);
        return new ResponseEntity<>("Event deleted successfully", HttpStatus.OK);
    }
    @PostMapping("events/{organizerId}/{eventId}/add-agenda")
    public ResponseEntity<String> addAgenda(
            @PathVariable Integer organizerId,
            @PathVariable Integer eventId,
            @RequestBody List<ActivityDTO> activities) {
        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return new ResponseEntity<>("Organizer not found", HttpStatus.NOT_FOUND);
        }

        Event event = eventService.findById(eventId);
        if (event == null || !event.getOrganizer().equals(organizer)) {
            return new ResponseEntity<>("Event not found or not owned by this organizer", HttpStatus.NOT_FOUND);
        }

        List<Activity> agenda = activities.stream().map(activityDTO -> {
            Activity activity = new Activity();
            activity.setName(activityDTO.getName());
            activity.setDescription(activityDTO.getDescription());
            activity.setTime(activityDTO.getTime());
            activity.setLocation(activityDTO.getLocation());
            return activity;
        }).collect(Collectors.toList());

        event.setActivities(agenda);
        eventService.save(event);

        return new ResponseEntity<>("Agenda added successfully", HttpStatus.OK);
    }

}
