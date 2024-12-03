package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.service.interfaces.EventTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventTypeServiceImpl implements EventTypeService {
    @Override
    public void save(EventType eventType) {

    }

    @Override
    public List<EventType> findAll() {
        return null;
    }

//    @Override
//    public Page<EventType> findAll(Pageable pageable) {
//        return eventTypeRepository.findAll(pageable);
//    }

    @Override
    public EventType findById(Integer id) {
        return null;
    }
}
