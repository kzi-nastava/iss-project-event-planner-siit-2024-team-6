package ftn.siit.project.isspoject.dto;

import ftn.siit.project.isspoject.entity.Category;
import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Offer;
import lombok.Data;

import java.util.List;

@Data
public class EventTypeDTO {
    private Integer id;
    private String name;
    private String description;
    private Boolean isDeleted;
}
