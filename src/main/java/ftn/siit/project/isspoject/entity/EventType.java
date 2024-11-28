package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.util.List;

@Data
public class EventType {
    private Integer id;
    private String name;
    private String description;
    private Boolean isDeleted;

    public EventType(){}
}
