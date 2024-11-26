package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.dto.ClosedEventDTO;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService{

    //@Autowired
    //private EventRepository eventRepository;

    @Override
    public List<Event> findAll() { return List.of();}

    @Override
    public Event findById(Integer eventId) { return null; }

    @Override
    public List<Event> findByOrganizer(Organizer organizer) {
        return List.of();
    }

    @Override
    public List<Event> findTopFive() { return List.of();}


    @Override
    public Event save(Event event) { return null;}

    @Override
    public void delete(Event event) {

    }

    @Override
    public List<Event> getEventsUserAttends(Integer userId) {
        return List.of();
    }

    public void addClosedEvent(ClosedEventDTO eventDTO) {
        Event event = new Event(eventDTO);
        //Event savedEvent = eventRepository.save(event);

        sendInvitations("",eventDTO.getEmails());
    }
    private void sendInvitations(String text,List<String> emails) {}
}
