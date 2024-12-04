package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.dto.event.ClosedEventDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.user.UserDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.service.interfaces.NotificationService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController 
@RequestMapping(value = "/api/events/")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private NotificationService notificationService;

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
    public ResponseEntity<UserDTO> addEventToFavorites(@PathVariable Integer userId, @PathVariable Integer eventId) {
        // Проверка существования пользователя
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Пользователь не найден
        }

        // Проверка существования события
        Event event = eventService.findById(eventId);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Событие не найдено
        }

        // Проверка, что событие уже добавлено в избранное
        if (user.getFavouriteEvents().contains(event)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Событие уже в избранном
        }

        // Добавление события в избранное
        user.getFavouriteEvents().add(event);
        User updatedUser = userService.save(user);

        // Преобразование в DTO
        UserDTO updatedUserDTO = toUserDTO(updatedUser);

        return ResponseEntity.ok(updatedUserDTO); // Возвращаем обновлённого пользователя
    }
    private UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setLastname(user.getLastname());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setPhotoUrl(user.getPhotoUrl());
        userDTO.setActive(user.getIsActive());
        userDTO.setSuspendedSince(user.getSuspendedSince());

        // Преобразование избранных событий
        List<EventDTO> favouriteEvents = user.getFavouriteEvents().stream().map(this::toEventDTO).collect(Collectors.toList());
        userDTO.setFavouriteEvents(favouriteEvents);

        return userDTO;
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
        eventDTO.setEventType(event.getEventType());
        eventDTO.setParticipants(event.getParticipants());
        return eventDTO;
    }

    @DeleteMapping("{userId}/{eventId}/favorite")
    public ResponseEntity<UserDTO> removeEventFromFavorites(@PathVariable Integer userId, @PathVariable Integer eventId) {
        // Проверка существования пользователя
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Пользователь не найден
        }

        // Проверка существования события
        Event event = eventService.findById(eventId);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Событие не найдено
        }

        // Проверка, что событие есть в избранном
        if (!user.getFavouriteEvents().contains(event)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Событие отсутствует в избранном
        }

        // Удаление события из избранного
        user.getFavouriteEvents().remove(event);
        User updatedUser = userService.save(user);

        // Преобразование в DTO
        UserDTO updatedUserDTO = toUserDTO(updatedUser);

        return ResponseEntity.ok(updatedUserDTO); // Возвращаем обновлённого пользователя
    }

    @GetMapping("{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable int id) {
        Event event = eventService.findById(id);

        if (event == null) {
            throw new NotFoundException("Event with id " + id + " not found");
        }

        EventDTO dto = new EventDTO(event);
        return ResponseEntity.ok(dto);
    }


    @GetMapping()
    public ResponseEntity<List<EventDTO>> getAll() {
        List<Event> events = eventService.findAll();
        List<EventDTO> dtos = events.stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

//    @GetMapping
//    public ResponseEntity<List<EventDTO>> getEventsPage(Pageable page) {
//
//        Page<Event> events = eventService.findAll(page);
//
//        List<EventDTO> eventDTOs = events.stream()
//                .map(EventDTO::new)
//                .toList();
//
//        return ResponseEntity.ok(eventDTOs);
//    }


//    @GetMapping(value = "/all_elements")
//    public ResponseEntity<PagedResponse<EventDTO>> getEventsPageAllElements(Pageable page) {
//
//        Page<Event> eventsPage = eventService.findAll(page);
//
//        List<EventDTO> eventDTOs = eventsPage.stream()
//                .map(EventDTO::new)
//                .toList();
//
//        PagedResponse<EventDTO> response = new PagedResponse<>(
//                eventDTOs,
//                eventsPage.getTotalPages(),
//                eventsPage.getTotalElements()
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


    @GetMapping("top-five")
    public ResponseEntity<List<EventDTO>> getTopFive() {
        List<Event> events = eventService.findTopFive();

        List<EventDTO> dtos = events.stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }
    @PostMapping("closed")
    public ResponseEntity<String> addClosed(@RequestBody ClosedEventDTO eventDTO) {
        eventService.addClosedEvent(eventDTO);
        return ResponseEntity.ok("Event created and invitations sent.");
    }

    @PutMapping("{id}")
    public ResponseEntity<EventDTO>  updateEvent(@PathVariable int id, @RequestBody EventDTO dto) {
        Event existingEvent = eventService.findById(id);
        if (existingEvent == null) {
            throw new NotFoundException("Event with id " + id + " not found, can't be updated");
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

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String place,
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {

        List<Event> filteredEvents = eventService.searchEvents(name, description, place, eventType, isPublic, startDate, endDate);
        return ResponseEntity.ok(filteredEvents);
    }
}
