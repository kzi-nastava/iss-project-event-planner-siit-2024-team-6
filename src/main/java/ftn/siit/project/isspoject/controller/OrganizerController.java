package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.activity.ActivityDTO;
import ftn.siit.project.isspoject.dto.activity.NewActivityDTO;
import ftn.siit.project.isspoject.dto.event.EventDTO;
import ftn.siit.project.isspoject.dto.event.NewEventDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.service.interfaces.*;
import ftn.siit.project.isspoject.dto.event.OrganizersEventDTO;
import ftn.siit.project.isspoject.entity.Activity;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.service.interfaces.EventService;
import ftn.siit.project.isspoject.service.interfaces.EventTypeService;
import ftn.siit.project.isspoject.service.interfaces.OrganizerService;
import ftn.siit.project.isspoject.service.external.PDFGeneratorService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @Autowired
    private ActivityService activityService;

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

    @GetMapping("events/{organizerId}")
    public ResponseEntity<List<OrganizersEventDTO>> getOrganizerEvents(@PathVariable Integer organizerId) {

        List<OrganizersEventDTO> events = eventService.findByOrganizerId(organizerId);

        for (OrganizersEventDTO event : events) {
            System.out.println(event);
          
        }

        return new ResponseEntity<>(events, HttpStatus.OK);
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
        Event updatedEvent = eventService.save(event);

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

//    @PostMapping("events/{organizerId}/{eventId}/add-agenda")
//    public ResponseEntity<EventDTO> addAgenda(
//            @PathVariable Integer organizerId,
//            @PathVariable Integer eventId,
//            @RequestBody List<NewActivityDTO> activities) {
//
//        Organizer organizer = organizerService.findById(organizerId);
//        if (organizer == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        Event event = eventService.findById(eventId);
////        if (event == null || !event.getOrganizer().equals(organizer)) {
////            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
////        }
//
//        List<Activity> agenda = activities.stream().map(activityDTO -> {
//            Activity activity = new Activity();
//            activity.setName(activityDTO.getName());
//            activity.setDescription(activityDTO.getDescription());
//            activity.setStartTime(activityDTO.getStart());
//            activity.setEndTime(activityDTO.getEnd());
//            activity.setLocation(activityDTO.getLocation());
//            return activity;
//        }).collect(Collectors.toList());
//
//        event.setEventActivities(agenda);
//        Event updatedEvent = eventService.save(event);
//
//        EventDTO updatedEventDTO = toEventDTO(updatedEvent);
//
//        return ResponseEntity.ok(updatedEventDTO);
//    }


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
