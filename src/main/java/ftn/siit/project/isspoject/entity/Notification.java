package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {
    private Integer id;
    private String text;
    private User receiver;
    private LocalDateTime timestamp;
}
