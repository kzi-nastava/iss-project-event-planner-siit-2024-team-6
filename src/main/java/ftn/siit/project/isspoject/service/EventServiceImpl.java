package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Organizer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService{
    @Override
    public void save(Event event) {

    }

    @Override
    public List<Event> findByOrganizer(Organizer organizer) {
        return null;
    }

    @Override
    public Event findById(Integer eventId) {
        return null;
    }

    @Override
    public void delete(Event event) {

    }
}
