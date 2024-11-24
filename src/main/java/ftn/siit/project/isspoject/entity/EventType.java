package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.util.List;

@Data
public class EventType {
    private String name;
    private String description;
    private boolean isDeleted;
    private List<Event> events;
    private List<Offer> offers;
}
