package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TimeSlot {
    private LocalDateTime start;
    private int duration;
    private List<Activity> activities;
}
