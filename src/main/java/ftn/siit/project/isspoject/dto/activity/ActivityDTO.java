package ftn.siit.project.isspoject.dto.activity;

import com.fasterxml.jackson.annotation.JsonFormat;
import ftn.siit.project.isspoject.entity.Activity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private Integer id;
    private String name;
    private String description;
    private String location;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endTime;

    public ActivityDTO(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.location = activity.getLocation();
        this.startTime = activity.getStartTime();
        this.endTime = activity.getEndTime();
    }
    public ActivityDTO() {
    }
}
