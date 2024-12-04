package ftn.siit.project.isspoject.dto.activity;

import ftn.siit.project.isspoject.entity.Event;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private String name;
    private String description;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;
    private Event event;
}
