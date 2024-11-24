package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.EventType;

import java.util.List;

public interface EventTypeService {
    void save(EventType eventType);

    List<EventType> findAll();

    EventType findById(Integer id);
}
