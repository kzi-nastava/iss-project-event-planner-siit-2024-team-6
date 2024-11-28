package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.ClosedEventDTO;
import ftn.siit.project.isspoject.entity.*;
import ftn.siit.project.isspoject.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class EventServiceImpl implements EventService{

    //@Autowired
    //private EventRepository eventRepository;

    @Override
    public List<Event> findAll() { return List.of(
            new Event(1, "Event 1", "Description 1", 100, 50, true, "Place 1", LocalDateTime.now(), new EventType(), new Organizer(), List.of(), new Budget()),
            new Event(2, "Event 2", "Description 2", 200, 150, false, "Place 2", LocalDateTime.now().plusDays(1), new EventType(), new Organizer(), List.of(), new Budget()),
            new Event(3, "Event 3", "Description 3", 50, 25, true, "Place 3", LocalDateTime.now().plusDays(2), new EventType(), new Organizer(), List.of(), new Budget()),
            new Event(4, "Event 4", "Description 4", 500, 300, false, "Place 4", LocalDateTime.now().plusDays(3), new EventType(), new Organizer(), List.of(), new Budget()),
            new Event(5, "Event 5", "Description 5", 300, 100, true, "Place 5", LocalDateTime.now().plusDays(4), new EventType(), new Organizer(), List.of(), new Budget()),
            new Event(6, "Event 6", "Description 6", 400, 200, false, "Place 6", LocalDateTime.now().plusDays(5), new EventType(), new Organizer(), List.of(), new Budget()),
            new Event(7, "Event 7", "Description 7", 250, 120, true, "Place 7", LocalDateTime.now().plusDays(6), new EventType(), new Organizer(), List.of(), new Budget())
    );}

    @Override
    public Event findById(Integer eventId) {
        if(eventId == 1){
            return new Event(1, "Event 1", "Description 1", 100, 50, true, "Place 1", LocalDateTime.now(), new EventType(), new Organizer(), List.of(), new Budget());
        } else if (eventId == 2) {
            return new Event(2, "Event 2", "Description 2", 200, 150, false, "Place 2", LocalDateTime.now().plusDays(1), new EventType(), new Organizer(), List.of(), new Budget());
        }
        return null;
    }

    @Override
    public List<Event> findByOrganizer(Organizer organizer) {
        return List.of();
    }

    @Override
    public List<Event> findTopFive() {
        return List.of(
                new Event(1, "Event 1", "Description 1", 100, 50, true, "Place 1", LocalDateTime.now(), new EventType(), new Organizer(), List.of(), new Budget()),
                new Event(2, "Event 2", "Description 2", 200, 150, false, "Place 2", LocalDateTime.now().plusDays(1), new EventType(), new Organizer(), List.of(), new Budget()),
                new Event(3, "Event 3", "Description 3", 50, 25, true, "Place 3", LocalDateTime.now().plusDays(2), new EventType(), new Organizer(), List.of(), new Budget()),
                new Event(4, "Event 4", "Description 4", 500, 300, false, "Place 4", LocalDateTime.now().plusDays(3), new EventType(), new Organizer(), List.of(), new Budget()),
                new Event(5, "Event 5", "Description 5", 300, 100, true, "Place 5", LocalDateTime.now().plusDays(4), new EventType(), new Organizer(), List.of(), new Budget())
        );
    }


    @Override
    public Event save(Event event) { return null;}

    @Override
    public void delete(Event event) {

    }

    @Override
    public List<Event> getEventsUserAttends(Integer userId) {
        return List.of(
                new Event(1, "Event 1", "Description 1", 100, 50, true, "Place 1", LocalDateTime.now(), new EventType(), new Organizer(), List.of(), new Budget())
                );
    }

    public void addClosedEvent(ClosedEventDTO eventDTO) {
        Event event = new Event(eventDTO);
        //Event savedEvent = eventRepository.save(event);

        sendInvitations("",eventDTO.getEmails());
    }
    private void sendInvitations(String text,List<String> emails) {}

//    public Page<Event> findAll(Pageable page) {
//        return eventRepository.findAll(page);
//    }

    public List<Event> searchEvents(String name, String description, String place, EventType eventType, Boolean isPublic, LocalDateTime startDate, LocalDateTime endDate) {
        //return eventRepository.searchEvents(name, description, place, eventType, isPublic, startDate, endDate);
        return List.of();
    }
}
