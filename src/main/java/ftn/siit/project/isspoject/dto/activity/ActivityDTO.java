package ftn.siit.project.isspoject.dto.activity;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.TimeSlot;
import lombok.Data;

@Data
public class ActivityDTO {
    private String name;
    private String description;
    private String location;
    private TimeSlot time;
    private Event event;
}
