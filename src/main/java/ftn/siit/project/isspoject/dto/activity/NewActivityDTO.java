package ftn.siit.project.isspoject.dto.activity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewActivityDTO {
    private String name;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
