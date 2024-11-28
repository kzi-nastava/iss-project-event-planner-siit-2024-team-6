package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.util.List;

@Data
public class EventType {
    private Integer id;
    private String name;
    private String description;
    private Boolean isDeleted;
    public EventType (){}
    public EventType(int i, String corporate, String professionalCorporateEvents, boolean b) {
        this.id = i;
        this.name = corporate;
        this.description = professionalCorporateEvents;
        this.isDeleted = b;
    }
}
