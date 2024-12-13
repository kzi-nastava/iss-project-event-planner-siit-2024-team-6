package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.EventTypeRepository;
import ftn.siit.project.isspoject.service.interfaces.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventTypeServiceImpl implements EventTypeService {

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Override
    public EventType save(EventType eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("EventType cannot be null while saving.");
        }
        return eventTypeRepository.save(eventType);
    }

    @Override
    public List<EventType> findAll() {
        List<EventType> eventTypes = eventTypeRepository.findAll();
        if (eventTypes.isEmpty()) {
            throw new NotFoundException("No event types found.");
        }
        return eventTypes;
    }

    @Override
    public EventType findById(Integer id) {
        return eventTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("EventType not found with ID: " + id));
    }

    @Override
    public EventType findByName(String name) {
        return eventTypeRepository.findByNameAndIsDeletedFalse(name)
                .orElseThrow(() -> new NotFoundException("EventType not found with name: " + name));
    }

//    @Override
//    public Page<EventType> findAll(Pageable pageable) {
//        return eventTypeRepository.findAll(pageable);
//    }

}
