package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.EventService;
import ftn.siit.project.isspoject.service.PDFGeneratorService;
import ftn.siit.project.isspoject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/events/")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private PDFGeneratorService pdfGeneratorService;

    @Autowired
    private UserService userService;

    @GetMapping("{eventId}/details")
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
    @GetMapping("{eventId}/generate-pdf")
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

    @PostMapping("{userId}/{eventId}/favorite")
    public ResponseEntity<String> addEventToFavorites(@PathVariable Integer userId, @PathVariable Integer eventId) {
        User user = userService.findById(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }

        if (user.getFavouriteEvents().contains(event)) {
            return new ResponseEntity<>("Event is already in favorites", HttpStatus.BAD_REQUEST);
        }

        user.getFavouriteEvents().add(event);
        userService.save(user);

        return new ResponseEntity<>("Event added to favorites", HttpStatus.OK);
    }
    @DeleteMapping("{userId}/{eventId}/favorite")
    public ResponseEntity<String> removeEventFromFavorites(@PathVariable Integer userId, @PathVariable Integer eventId) {
        User user = userService.findById(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Event event = eventService.findById(eventId);
        if (event == null) {
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }

        if (!user.getFavouriteEvents().contains(event)) {
            return new ResponseEntity<>("Event is not in favorites", HttpStatus.BAD_REQUEST);
        }

        user.getFavouriteEvents().remove(event);
        userService.save(user);

        return new ResponseEntity<>("Event removed from favorites", HttpStatus.OK);
    }
}
