package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.service.EventService;
import ftn.siit.project.isspoject.service.PDFGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private PDFGeneratorService pdfGeneratorService;

    @GetMapping("/{eventId}/details")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        EventDTO eventDTO = new EventDTO();
        eventDTO.setName(event.getName());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setPlace(event.getPlace());
        eventDTO.setDate(event.getDate());
        eventDTO.setMaxParticipants(event.getMaxParticipants());
        eventDTO.setIsPublic(event.getIsPublic());

        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/generate-pdf")
    public ResponseEntity<byte[]> generateEventPDF(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] pdf = pdfGeneratorService.generateEventPDF(event);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=event-details.pdf")
                .body(pdf);
    }
}
