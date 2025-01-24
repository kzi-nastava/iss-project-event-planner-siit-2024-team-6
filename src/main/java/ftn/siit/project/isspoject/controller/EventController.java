package ftn.siit.project.isspoject.controller;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.pagination.PagedResponse;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.dto.user.OrganizerDTO;
import ftn.siit.project.isspoject.dto.user.UserDTO;
import ftn.siit.project.isspoject.dto.event.NewClosedEventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.implementations.EventTypeServiceImpl;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController 
@RequestMapping(value = "/api/events/")
@CrossOrigin(origins = "http://localhost:4200")
public class EventController {

    @Autowired
    private EventService eventService;


    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrganizerService organizerService;

    @Autowired
    private EventTypeService eventTypeService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenUtils tokenUtils;
    @GetMapping("{eventId}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);

        EventDTO eventDTO = new EventDTO(event);
        
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }
    @GetMapping("{eventId}/getOrganizer")
    public ResponseEntity<OrganizerDTO> getEventOrganizer(@PathVariable Integer eventId) {
        User organizer = organizerService.findOrganizerByEventId(eventId);

        OrganizerDTO organizerDTO = new OrganizerDTO((Organizer) organizer);

        return new ResponseEntity<>(organizerDTO, HttpStatus.OK);
    }
    @GetMapping("{eventId}/getInfoPDF")
    public ResponseEntity<byte[]> generateEventPDF(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);

        byte[] pdfContent = pdfGeneratorService.generateEventPDF(event);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentDispositionFormData("attachment", "document.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
    @GetMapping("{eventId}/getEventStatisticsPDF")
    public ResponseEntity<byte[]> downloadEventStatisticsPDF(@PathVariable Integer eventId) {
        Event event = eventService.findById(eventId);

        byte[] pdfContent = pdfGeneratorService.downloadEventStatisticsPDF(event);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentDispositionFormData("attachment", "document.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
    @PostMapping("{eventId}/favorite")
    public ResponseEntity<UserDTO> addEventToFavorites(@PathVariable Integer eventId, HttpServletRequest request) {
        // Проверка существования пользователя
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Проверка существования события
        Event event = eventService.findById(eventId);

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

    @GetMapping("unPagedFavorites")
    public ResponseEntity<List<EventDTO>> getFavorites(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Event> events = user.getFavouriteEvents();

        List<EventDTO> ed = new ArrayList<>();

        for (Event e: events){
            ed.add(new EventDTO(e));
        }

        return ResponseEntity.ok(ed);

    }

    @GetMapping("favorites")
    public ResponseEntity<PagedResponse<EventDTO>> getPagedFavorites(
            HttpServletRequest request,
            Pageable pageable
    ) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Event> events = user.getFavouriteEvents();

        List<EventDTO> eventDTOs = events.stream()
                .map(EventDTO::new)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), eventDTOs.size());
        List<EventDTO> paginatedEvents = eventDTOs.subList(start, end);

        PagedResponse<EventDTO> response = new PagedResponse<>(paginatedEvents,
                (int) Math.ceil((double) eventDTOs.size() / pageable.getPageSize()),
                eventDTOs.size());

        return ResponseEntity.ok(response);
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
        eventDTO.setEventType(new EventTypeDTO(event.getEventType()));
        eventDTO.setParticipants(event.getParticipants());
        return eventDTO;
    }

    @DeleteMapping("{eventId}/favorite")
    public ResponseEntity<UserDTO> removeEventFromFavorites( @PathVariable Integer eventId, HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Проверка существования события
        Event event = eventService.findById(eventId);

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

//    @GetMapping("{id}")
//    public ResponseEntity<EventDTO> getEvent(@PathVariable int id) {
//        Event event = eventService.findById(id);
//
//        EventDTO dto = new EventDTO(event);
//        return ResponseEntity.ok(dto);
//    }


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


    @GetMapping(value = "/all-elements")
    public ResponseEntity<PagedResponse<EventDTO>> getEventsPageAllElements(Pageable page) {

        Page<Event> eventsPage = eventService.findAll(page);

        List<EventDTO> eventDTOs = eventsPage.stream()
                .map(EventDTO::new)
                .toList();

        PagedResponse<EventDTO> response = new PagedResponse<>(
                eventDTOs,
                eventsPage.getTotalPages(),
                eventsPage.getTotalElements()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("top-five")
    public ResponseEntity<List<EventDTO>> getTopFive() {
        List<Event> events = eventService.findTopFive();

        List<EventDTO> dtos = events.stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<Event>> searchEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String place,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int pageSize) {

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null && !startDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
        }

        if (endDate != null && !endDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            endDateTime = LocalDate.parse(endDate, formatter).atTime(LocalTime.MAX);
        }


        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Event> filteredEvents = eventService.searchEvents(name, description, place, eventType, startDateTime, endDateTime, pageable);
        if (filteredEvents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(filteredEvents);
    }
    @GetMapping("{categoryId}/event-types-by-category")
    public ResponseEntity<List<EventTypeDTO>> getEventTypesByCategory( @PathVariable Integer categoryId, HttpServletRequest request){
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(eventTypeService.findAllWithCategoryId(categoryId));
    }
    @GetMapping("{categoryName}/event-types-by-category-name")
    public ResponseEntity<List<EventTypeDTO>> getEventTypesByCategoryName( @PathVariable String categoryName, HttpServletRequest request){
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(eventTypeService.findAllWithCategoryId(categoryService.findByName(categoryName).getId()));
    }
    @GetMapping("{name}/event-type")
    public ResponseEntity<EventTypeDTO> getEventType(@PathVariable String name) {
        EventType eventType = eventTypeService.findByName(name);
        return ResponseEntity.ok(new EventTypeDTO(eventType));
    }

    @GetMapping("event-types")
    public ResponseEntity<List<String>> getAllEventTypes() {
        List<String> eventTypes = eventTypeService.findAllNames();
        if (eventTypes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(eventTypes);
    }
}
