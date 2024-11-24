package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.service.EventService;
import ftn.siit.project.isspoject.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/organizers")
public class OrganizerController {
    @Autowired
    private EventService eventService;

    @Autowired
    private OrganizerService organizerService;

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

}
