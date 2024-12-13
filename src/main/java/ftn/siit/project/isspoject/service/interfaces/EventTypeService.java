package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.EventType;

import java.util.List;

public interface EventTypeService {
    EventType save(EventType eventType);

    List<EventType> findAll();

    EventType findById(Integer id);

    EventType findByName(String name);
}
