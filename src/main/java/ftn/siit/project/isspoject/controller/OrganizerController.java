package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.activity.NewActivityDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.dto.event.OrganizersEventDTO;
import ftn.siit.project.isspoject.entity.Activity;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.EventTypeService;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/organizers")
public class OrganizerController {
    @Autowired
    private EventService eventService;

    @Autowired
    private OrganizerService organizerService;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private EventTypeService eventTypeService;

    @PostMapping("/events")
    public ResponseEntity<EventDTO> createEvent(@RequestBody NewEventDTO eventDTO, HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer organizer = (Organizer) userService.findByEmail(email);

        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

//        Organizer organizer = organizerService.findById(organizerId);
//        if (organizer == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Организатор не найден
//        }

        // Создание нового события
        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setMaxParticipants(eventDTO.getMaxParticipants());
        event.setIsPublic(eventDTO.getIsPublic());
        event.setPlace(eventDTO.getPlace());
        event.setDate(eventDTO.getDate());
        event.setEventType(eventTypeService.findByName(eventDTO.getEventType().getName()));
        event.setParticipants(0);
        event.setPhotos(eventDTO.getPhotos());

        List<Event> myEvents = organizer.getMyEvents();
        myEvents.add(event);
        organizer.setMyEvents(myEvents);
//        // Устанавливаем организатора
//        event.setOrganizer(organizer);

        // Сохраняем событие
        Event savedEvent = eventService.save(event);
        userService.save(organizer);
        // Преобразование в DTO
        EventDTO responseDTO = toEventDTO(savedEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO); // Возвращаем созданное событие
    }

    @GetMapping("events/{organizerId}")
    public ResponseEntity<List<OrganizersEventDTO>> getOrganizerEvents(@PathVariable Integer organizerId) {

        List<OrganizersEventDTO> events = eventService.findByOrganizerId(organizerId);

        for (OrganizersEventDTO event : events) {
            System.out.println(event);
        }

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PutMapping("events/{organizerId}/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Integer organizerId,
            @PathVariable Integer eventId,
            @RequestBody EventDTO eventDTO) {

        // Проверка наличия организатора
        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Организатор не найден
        }

        // Проверка наличия события и его принадлежности организатору
        Event event = eventService.findById(eventId);
//        if (event == null || !event.getOrganizer().equals(organizer)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Событие не найдено или не принадлежит организатору
//        }

        // Обновление полей события
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setMaxParticipants(eventDTO.getMaxParticipants());
        event.setIsPublic(eventDTO.getIsPublic());
        event.setPlace(eventDTO.getPlace());
        event.setDate(eventDTO.getDate());

        Event updatedEvent = eventService.save(event);

        // Преобразование в DTO
        EventDTO updatedEventDTO = toEventDTO(updatedEvent);

        return ResponseEntity.ok(updatedEventDTO); // Возвращаем обновлённое событие
    }
    private EventDTO toEventDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setName(event.getName());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setMaxParticipants(event.getMaxParticipants());
        eventDTO.setIsPublic(event.getIsPublic());
        eventDTO.setPlace(event.getPlace());
        eventDTO.setDate(event.getDate());
        return eventDTO;
    }

    @DeleteMapping("events/{organizerId}/{eventId}")
    public ResponseEntity<EventDTO> deleteEvent(@PathVariable Integer organizerId, @PathVariable Integer eventId) {

        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


        Event event = eventService.findById(eventId);
//        if (event == null || !event.getOrganizer().equals(organizer)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }


        eventService.delete(event);


        EventDTO deletedEventDTO = toEventDTO(event);

        return ResponseEntity.ok(deletedEventDTO);
    }

    @PostMapping("events/{organizerId}/{eventId}/add-agenda")
    public ResponseEntity<EventDTO> addAgenda(
            @PathVariable Integer organizerId,
            @PathVariable Integer eventId,
            @RequestBody List<NewActivityDTO> activities) {

        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Event event = eventService.findById(eventId);
//        if (event == null || !event.getOrganizer().equals(organizer)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }

        List<Activity> agenda = activities.stream().map(activityDTO -> {
            Activity activity = new Activity();
            activity.setName(activityDTO.getName());
            activity.setDescription(activityDTO.getDescription());
            activity.setStartTime(activityDTO.getStart());
            activity.setEndTime(activityDTO.getEnd());
            activity.setLocation(activityDTO.getLocation());
            return activity;
        }).collect(Collectors.toList());

        event.setEventActivities(agenda);
        Event updatedEvent = eventService.save(event);

        EventDTO updatedEventDTO = toEventDTO(updatedEvent);

        return ResponseEntity.ok(updatedEventDTO);
    }


    @GetMapping("events/{organizerId}/{eventId}/agenda")
    public ResponseEntity<List<NewActivityDTO>> getAgenda(
            @PathVariable Integer organizerId,
            @PathVariable Integer eventId) {
        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Event event = eventService.findById(eventId);
//        if (event == null || !event.getOrganizer().equals(organizer)) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }

        List<NewActivityDTO> agenda = event.getEventActivities().stream().map(activity -> {
            NewActivityDTO dto = new NewActivityDTO();
            dto.setName(activity.getName());
            dto.setDescription(activity.getDescription());
            dto.setStart(activity.getStartTime());
            dto.setEnd(activity.getEndTime());
            dto.setLocation(activity.getLocation());
            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @GetMapping("events/{organizerId}/{eventId}/generate-pdf")
    public ResponseEntity<byte[]> generateGuestListPDF(
            @PathVariable Integer organizerId,
            @PathVariable Integer eventId) {
        Organizer organizer = organizerService.findById(organizerId);
        if (organizer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Event event = eventService.findById(eventId);
//        if (event == null || !event.getOrganizer().equals(organizer)) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }

        byte[] pdf = pdfGeneratorService.generateGuestListPDF(event);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=guest-list.pdf")
                .body(pdf);
    }
}
