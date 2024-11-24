package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.EventService;
import ftn.siit.project.isspoject.service.NotificationService;
import ftn.siit.project.isspoject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/events")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    NotificationService notificationService;


    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable int id) {
        Event event = eventService.findById(id);

        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        EventDTO dto = new EventDTO(event);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/all")
    public ResponseEntity<List<EventDTO>> getAll() {
        List<Event> events = eventService.findAll();
        if (events == null) {
            return ResponseEntity.noContent().build();
        }

        List<EventDTO> dtos = eventService.findAll().stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/top-five")
    public ResponseEntity<List<EventDTO>> getTopFive() {
        List<Event> events = eventService.findAll();
        if (events == null) {
            return ResponseEntity.noContent().build();
        }

        List<EventDTO> dtos = eventService.findAll().stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EventDTO>  updateEvent(@PathVariable int id, @RequestBody EventDTO dto) {
        Event existingEvent = eventService.findById(id);
        if (existingEvent == null) {
            return ResponseEntity.notFound().build();
        }

        existingEvent.setName(dto.getName());
        existingEvent.setDescription(dto.getDescription());
        existingEvent.setMaxParticipants(dto.getMaxParticipants());
        existingEvent.setParticipants(dto.getParticipants());
        existingEvent.setIsPublic(dto.getIsPublic());
        existingEvent.setPlace(dto.getPlace());
        existingEvent.setDate(dto.getDate());
        existingEvent.setEventType(dto.getEventType());

        Event updatedEvent = eventService.save(existingEvent);

        List<User> attendees = userService.findEventAttendees(id);
        String notificationMessage = "The event '" + updatedEvent.getName() + "' has been updated.";
        notificationService.notifyUsers(attendees, notificationMessage);

        return ResponseEntity.ok(new EventDTO(updatedEvent));


    }
}
