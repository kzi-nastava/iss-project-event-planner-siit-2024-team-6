package ftn.siit.project.isspoject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarItemDTO {
    private String name;
    private LocalDateTime date;
    private String type;

    public CalendarItemDTO(String name, LocalDateTime date, String type) {
        this.name = name;
        this.date = date;
        this.type = type;
    }

}
