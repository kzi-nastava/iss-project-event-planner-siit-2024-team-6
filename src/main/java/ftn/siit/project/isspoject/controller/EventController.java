package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/events")
public class EventController {

    @Autowired
    private EventService eventService;


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
}
