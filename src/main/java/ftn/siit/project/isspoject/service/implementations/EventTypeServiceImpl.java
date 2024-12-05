package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.EventType;
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
        return eventTypeRepository.save(eventType);
    }

    @Override
    public List<EventType> findAll() {
        return eventTypeRepository.findAll();
    }

//    @Override
//    public Page<EventType> findAll(Pageable pageable) {
//        return eventTypeRepository.findAll(pageable);
//    }

    @Override
    public EventType findById(Integer id) {
        return eventTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EventType not found with ID: " + id));
    }
}
