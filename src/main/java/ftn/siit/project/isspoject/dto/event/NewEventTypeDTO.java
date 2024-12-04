package ftn.siit.project.isspoject.dto.event;

import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Offer;
import lombok.Data;

import java.util.List;

@Data
public class NewEventTypeDTO {
    private String name;
    private String description;
    private Boolean isDeleted;
}
