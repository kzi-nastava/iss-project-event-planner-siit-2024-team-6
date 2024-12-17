package ftn.siit.project.isspoject.dto.event;

import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.EventType;
import ftn.siit.project.isspoject.entity.Offer;
import lombok.Data;

import java.util.List;

@Data
public class EventTypeDTO {
    private Integer id;
    private String name;
    private String description;
    private Boolean isDeleted;

    public EventTypeDTO(){}
    public EventTypeDTO(EventType eventType) {
        this.id = eventType.getId();
        this.name = eventType.getName();
        this.description = eventType.getDescription();
        this.isDeleted = this.getIsDeleted();
    }
}
