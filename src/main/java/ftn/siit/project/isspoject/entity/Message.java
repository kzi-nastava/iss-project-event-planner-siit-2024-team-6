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
    private Integer sender;
    private Integer receiver;
    public Message(){}
    public Message (MessageDTO dto, int sender, int receiver) {
        this.id = dto.getId();
        this.text = dto.getText();
        this.time = dto.getTime();
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message(Integer integer, String s, ZonedDateTime zonedDateTime, int i, int j) {
        this.id = integer;
        this.text = s;
        this.time = zonedDateTime;
        this.sender = i;
        this.receiver = j;
    }
}
