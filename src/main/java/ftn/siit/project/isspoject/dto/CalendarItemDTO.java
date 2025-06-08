package ftn.siit.project.isspoject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarItemDTO {
    private String name;
    private LocalDateTime date;
    private String type;
    private Integer id;

    public CalendarItemDTO(String name, LocalDateTime date, String type, Integer id) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.id = id;
    }

    public CalendarItemDTO(String name, LocalDateTime date, String type) {
        this.name = name;
        this.date = date;
        this.type = type;
    }

}
