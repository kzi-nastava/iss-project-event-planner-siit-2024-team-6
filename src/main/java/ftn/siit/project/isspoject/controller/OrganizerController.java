package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.activity.ActivityDTO;
import ftn.siit.project.isspoject.dto.activity.NewActivityDTO;
import ftn.siit.project.isspoject.dto.budget.BudgetDTO;
import ftn.siit.project.isspoject.dto.budget.BudgetItemDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetDTO;
import ftn.siit.project.isspoject.dto.budget.NewBudgetItemDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.EventTypeDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.dto.pagination.PagedResponse;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/organizers")
public class OrganizerController {
    @Autowired
    private EventService eventService;
    @Autowired
    private NotificationService notificationService;
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
    @Autowired
    private ActivityService activityService;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private CategoryService categoryService;

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

        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setMaxParticipants(eventDTO.getMaxParticipants());
        event.setIsPublic(eventDTO.getIsPublic());
        event.setPlace(eventDTO.getPlace());
        event.setDate(eventDTO.getDate());
        event.setEventType(eventTypeService.findByName(eventDTO.getEventType()));
        event.setParticipants(0);
        event.setPhotos(eventDTO.getPhotos());
        event.setIsDeleted(false);
        Budget budget = new Budget();
        budget.setAvailable(0);
        budget.setTotal(0);
        Budget b = budgetService.save(budget);

        event.setBudget(b);

        List<Event> myEvents = organizer.getMyEvents();
        myEvents.add(event);
        organizer.setMyEvents(myEvents);

        Event savedEvent = eventService.save(event);
        userService.save(organizer);

        EventDTO responseDTO = toEventDTO(savedEvent);

        if(eventDTO.getIsPublic() == false) {
            eventService.sendInvitations(responseDTO, eventDTO.getEmails());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("category-names")
    public ResponseEntity<List<String>> getAllCategoryNames(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<String> categories = categoryService.findAllNames();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    @PostMapping("budget/{budgetId}/items")
    public ResponseEntity<BudgetItemDTO> addItemToBudget(@PathVariable int budgetId, @RequestBody NewBudgetItemDTO budgetDTO, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer o = organizerService.findByEmail(email);
        if (o == null) {
             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(eventService.checkIfEventHasPassed(budgetId, o)){
            throw new IllegalArgumentException("Event has passed. Cannot access its budget.");
        }
        BudgetItem bi = budgetService.addItemToBudget(budgetId, budgetDTO.getCategory(), budgetDTO.getMaxPrice());
        return ResponseEntity.ok(new BudgetItemDTO(bi));
    }

    @PutMapping("budget/{budgetId}/items/{itemId}")
    public ResponseEntity<BudgetItemDTO> updateBudgetItem(@PathVariable int budgetId, @PathVariable int itemId, @RequestBody double price, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer o = organizerService.findByEmail(email);
        if (o == null) {
             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(eventService.checkIfEventHasPassed(budgetId, o)){
            throw new IllegalArgumentException("Event has passed. Cannot access its budget.");
        }
        BudgetItem bi = budgetService.updateBudgetItem(budgetId, itemId, price);
        return ResponseEntity.ok(new BudgetItemDTO(bi));
    }

    @DeleteMapping("budget/{budgetId}/items/{itemId}")
    public ResponseEntity<Void> deleteBudgetItem(@PathVariable int budgetId, @PathVariable int itemId, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer o = organizerService.findByEmail(email);
        if (o == null) {
             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(eventService.checkIfEventHasPassed(budgetId, o)){
            throw new IllegalArgumentException("Event has passed. Cannot access its budget.");
        }
        budgetService.removeBudgetItem(budgetId, itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("events")
    public ResponseEntity<List<EventDTO>> getOrganizerEvents(HttpServletRequest request) {
//        Organizer organizer = organizerService.findById(organizerId);
//        if (organizer == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = (Organizer) userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<Event> events = eventService.findByOrganizer(user);
        List<EventDTO> eventDTOs = new ArrayList<>();
        for(Event e: events){
            eventDTOs.add(new EventDTO(e));
        }

        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }
    @GetMapping("paged-events")
    public ResponseEntity<PagedResponse<EventDTO>> getOrganizerPagedEvents(            @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @RequestParam(defaultValue = "asc") String sortDir,
                                                                              HttpServletRequest request) {
//        Organizer organizer = organizerService.findById(organizerId);
//        if (organizer == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = (Organizer) userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "date"));

        Page<Event> eventsPage = eventService.findByOrganizer(pageable, user);
        List<Event> events = eventService.findByOrganizer(user);
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
    @GetMapping("{id}/events")
    public ResponseEntity<List<EventDTO>> getEvents(@PathVariable int id) {
        Organizer organizer = organizerService.findById(id);
        if (organizer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Event> events = eventService.findByOrganizer(organizer);
        List<EventDTO> eventDTOs = new ArrayList<>();
        for(Event e: events){
            eventDTOs.add(new EventDTO(e));
        }

        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @GetMapping("future-events")
    public ResponseEntity<List<EventDTO>> getFutureEvents(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer organizer = (Organizer) userService.findByEmail(email);
        if (organizer == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Event> events = eventService.getFutureEvents(organizer);
        List<EventDTO> eventDTOs = new ArrayList<>();
        for(Event e: events){
            eventDTOs.add(new EventDTO(e));
        }

        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @PutMapping("events/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Integer eventId,
            @RequestBody EventDTO eventDTO,
            HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = (Organizer) userService.findByEmail(email);


        // Проверка наличия события и его принадлежности организатору
        Event event = eventService.findById(eventId);
        if(! user.getMyEvents().contains(event)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
//        if (event == null || !event.getOrganizer().equals(organizer)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Событие не найдено или не принадлежит организатору
//        }

        // Обновление полей события
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setMaxParticipants(eventDTO.getMaxParticipants());
        event.setPlace(eventDTO.getPlace());
        event.setDate(eventDTO.getDate());
        event.setIsDeleted(eventDTO.getIsDeleted());
        event.setPhotos(eventDTO.getPhotos());
        Event updatedEvent = eventService.save(event);
        //notifications sending
        List<User> attendees = userService.findEventAttendees(eventId);
        String notificationMessage = "The event '" + updatedEvent.getName() + "' has been updated. Please check the latest updates.";
        notificationService.notifyUsers(attendees, notificationMessage);

        // Преобразование в DTO
        EventDTO updatedEventDTO = new EventDTO(updatedEvent);

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
        eventDTO.setEventType(new EventTypeDTO(event.getEventType()));
        eventDTO.setPhotos(event.getPhotos());
        eventDTO.setIsDeleted(event.getIsDeleted());
        return eventDTO;
    }

    @DeleteMapping("events/{eventId}")
    public ResponseEntity<EventDTO> deleteEvent(@PathVariable Integer eventId,
                                                HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = (Organizer) userService.findByEmail(email);


        // Проверка наличия события и его принадлежности организатору
        Event event = eventService.findById(eventId);
        if(! user.getMyEvents().contains(event)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


//        if (event == null || !event.getOrganizer().equals(organizer)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }


        eventService.delete(event);


        EventDTO deletedEventDTO = toEventDTO(event);

        return ResponseEntity.ok(deletedEventDTO);
    }
    @PostMapping("events/{eventId}/activity")
    public ResponseEntity<ActivityDTO> addActivity(@PathVariable Integer eventId,
                                                   @RequestBody NewActivityDTO activityDTO,
                                                   HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = (Organizer) userService.findByEmail(email);


        // Проверка наличия события и его принадлежности организатору
        Event event = eventService.findById(eventId);
        if(! user.getMyEvents().contains(event)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Activity> activities = event.getEventActivities();

        Activity activity = activityService.save(new Activity(activityDTO));

        activities.add(activity);
        event.setEventActivities(activities);

        eventService.save(event);

        return ResponseEntity.ok(new ActivityDTO(activity));
    }
    @GetMapping("events/{eventId}/activity/{activityId}")
    public ResponseEntity<ActivityDTO> getActivity(@PathVariable Integer eventId,
                                                   @PathVariable Integer activityId,
                                                   HttpServletRequest request) {

        Activity activity = activityService.findById(activityId);

        return ResponseEntity.ok(new ActivityDTO(activity));
    }
    @PutMapping("events/{eventId}/activity/{activityId}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Integer eventId,
                                                      @PathVariable Integer activityId,
                                                      @RequestBody NewActivityDTO activityDTO,
                                                      HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = (Organizer) userService.findByEmail(email);


        // Проверка наличия события и его принадлежности организатору
        Event event = eventService.findById(eventId);
        if(! user.getMyEvents().contains(event)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Activity> activities = event.getEventActivities();

        Activity activity = null;

        for(Activity a: activities){
            if(a.getId().equals(activityId)){
                activity = a;
            }
        }

        activity = activityService.update(activity, activityDTO);

        activities.add(activity);
        event.setEventActivities(activities);

        eventService.save(event);

        return ResponseEntity.ok(new ActivityDTO(activity));
    }

    @DeleteMapping("events/{eventId}/activity/{activityId}")
    public ResponseEntity<ActivityDTO> deleteActivity(@PathVariable Integer eventId,
                                                      @PathVariable Integer activityId,
                                                      HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = (Organizer) userService.findByEmail(email);


        // Проверка наличия события и его принадлежности организатору
        Event event = eventService.findById(eventId);
        if(! user.getMyEvents().contains(event)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Activity> activities = event.getEventActivities();

        Activity activity = activityService.findById(activityId);

        activities.remove(activity);

        event.setEventActivities(activities);

        eventService.save(event);
        activityService.delete(activity);

        return ResponseEntity.ok(new ActivityDTO(activity));
    }

    @GetMapping("events/{eventId}/agenda")
    public ResponseEntity<List<ActivityDTO>> getAgenda(
            @PathVariable Integer eventId,
            HttpServletRequest request) {

        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        Organizer user = (Organizer) userService.findByEmail(email);

        Event event = eventService.findById(eventId);

        boolean contains = false;

        for(Event e: user.getMyEvents()){
            if(e.getId().equals(eventId)){
                contains = true;
            }
        }

        if(!contains){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        List<ActivityDTO> agenda = event.getEventActivities().stream().map(ActivityDTO::new).collect(Collectors.toList());

        return new ResponseEntity<>(agenda, HttpStatus.OK);
    }

    @GetMapping("events/{eventId}/getAgendaPDF")
    public ResponseEntity<byte[]> getAgendaPDF(
            @PathVariable Integer eventId) {

        Event event = eventService.findById(eventId);

        byte[] pdfContent = pdfGeneratorService.generateAgendaPdf(event.getEventActivities());

        HttpHeaders headers = new HttpHeaders();

        headers.setContentDispositionFormData("attachment", "document.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);

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

        byte[] pdf =null;

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=guest-list.pdf")
                .body(pdf);
    }
}