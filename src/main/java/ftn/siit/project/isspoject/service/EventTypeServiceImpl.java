package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class EventTypeServiceImpl implements EventTypeService{
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
