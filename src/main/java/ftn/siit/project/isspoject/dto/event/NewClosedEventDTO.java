package ftn.siit.project.isspoject.dto.event;

import ftn.siit.project.isspoject.entity.Event;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewClosedEventDTO {

    private String name;
    private String description;
    private Integer maxParticipants;
    private String place;
    private LocalDateTime date;
    private Integer eventTypeId;
    private List<String> emails = new ArrayList<>();
    public NewClosedEventDTO() {
        super();
    }

}
