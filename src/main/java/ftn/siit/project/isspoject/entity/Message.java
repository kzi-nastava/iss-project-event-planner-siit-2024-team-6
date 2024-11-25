package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.MessageDTO;
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
    public Message (MessageDTO dto, User sender, User receiver) {
        this.id = dto.getId();
        this.text = dto.getText();
        this.time = dto.getTime();
        this.sender = sender;
        this.receiver = receiver;
    }
}
