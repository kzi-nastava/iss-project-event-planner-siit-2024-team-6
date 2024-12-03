package ftn.siit.project.isspoject.dto.message;

import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.entity.User;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class MessageDTO {
    private Integer id;
    private String text;
    private ZonedDateTime time;

    public MessageDTO() {}

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.text = message.getText();
        this.time = message.getTime();
    }
}
