package ftn.siit.project.isspoject.entity;

import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;

@Data
public class Message {
    private Integer id;
    private String text;
    private ZonedDateTime time;
    private User sender;
    private User receiver;
}
