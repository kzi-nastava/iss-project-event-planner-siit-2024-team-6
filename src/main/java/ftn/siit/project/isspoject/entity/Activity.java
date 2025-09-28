package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.activity.NewActivityDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Activity(NewActivityDTO activityDTO) {
        this.name = activityDTO.getName();
        this.description = activityDTO.getDescription();
        this.location = activityDTO.getLocation();
        this.startTime = activityDTO.getStartTime();
        this.endTime = activityDTO.getEndTime();
    }

    public Activity() {

    }
}
