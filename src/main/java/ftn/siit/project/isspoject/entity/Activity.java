package ftn.siit.project.isspoject.entity;

import lombok.Data;

@Data
public class Activity {
    private String name;
    private String description;
    private String location;
    private TimeSlot time;
    private Event event;
}
