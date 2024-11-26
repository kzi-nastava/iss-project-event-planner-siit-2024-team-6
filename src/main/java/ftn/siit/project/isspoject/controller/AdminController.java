package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.dto.EventTypeDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.service.EventService;
import ftn.siit.project.isspoject.service.EventTypeService;
import ftn.siit.project.isspoject.service.PDFGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/admins/")
public class AdminController {

    @Autowired
    private EventTypeService eventTypeService;
    @Autowired
    private EventService eventService;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @PostMapping("event-types/add")
    public ResponseEntity<String> addEventType(@RequestBody EventTypeDTO eventTypeDTO) {
        if (eventTypeDTO == null || eventTypeDTO.getName() == null || eventTypeDTO.getDescription() == null) {
            return new ResponseEntity<>("Invalid event type data", HttpStatus.BAD_REQUEST);
        }

        EventType eventType = new EventType();
        eventType.setName(eventTypeDTO.getName());
        eventType.setDescription(eventTypeDTO.getDescription());
        eventType.setCategories(eventTypeDTO.getCategories());
        eventType.setIsDeleted(false);

        eventTypeService.save(eventType);
        return new ResponseEntity<>("Event type added successfully", HttpStatus.CREATED);
    }
    @GetMapping("event-types/all")
    public ResponseEntity<List<EventTypeDTO>> getAllEventTypes() {
        List<EventType> eventTypes = eventTypeService.findAll();
        List<EventTypeDTO> eventTypeDTOs = eventTypes.stream().map(eventType -> {
            EventTypeDTO dto = new EventTypeDTO();
            dto.setName(eventType.getName());
            dto.setDescription(eventType.getDescription());
            dto.setCategories(eventType.getCategories());
            dto.setIsDeleted(eventType.getIsDeleted());
            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(eventTypeDTOs, HttpStatus.OK);
    }

    @PutMapping("event-types/{id}/update")
    public ResponseEntity<String> updateEventType(
            @PathVariable Integer id,
            @RequestBody EventTypeDTO eventTypeDTO) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return new ResponseEntity<>("Event type not found", HttpStatus.NOT_FOUND);
        }

        eventType.setDescription(eventTypeDTO.getDescription());
        eventType.setCategories(eventTypeDTO.getCategories());
        eventTypeService.save(eventType);

        return new ResponseEntity<>("Event type updated successfully", HttpStatus.OK);
    }
    @PutMapping("event-types/{id}/activate")
    public ResponseEntity<String> activateEventType(@PathVariable Integer id) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return new ResponseEntity<>("Event type not found", HttpStatus.NOT_FOUND);
        }

        eventType.setIsDeleted(false);
        eventTypeService.save(eventType);
        return new ResponseEntity<>("Event type activated", HttpStatus.OK);
    }
    @PutMapping("event-types/{id}/deactivate")
    public ResponseEntity<String> deactivateEventType(@PathVariable Integer id) {
        EventType eventType = eventTypeService.findById(id);
        if (eventType == null) {
            return new ResponseEntity<>("Event type not found", HttpStatus.NOT_FOUND);
        }

        eventType.setIsDeleted(true);
        eventTypeService.save(eventType);
        return new ResponseEntity<>("Event type deactivated", HttpStatus.OK);
    }

    @GetMapping("{eventId}/analytics")
    public ResponseEntity<EventDTO> getEventAnalytics(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        EventDTO analytics = new EventDTO();
//        Some logic for analysis
//        analytics.setEventName(event.getName());
//        analytics.setTotalAttendees(event.getGuests().size());
//        analytics.setRatingsDistribution(eventService.getRatingsDistribution(event));
//        analytics.setAverageRating(eventService.getAverageRating(event));

        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("{eventId}/generate-analytics-pdf")
    public ResponseEntity<byte[]> generateAnalyticsPDF(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] pdf = pdfGeneratorService.generateEventAnalyticsPDF(event);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=event-analytics.pdf")
                .body(pdf);
    }
}
