package ftn.siit.project.isspoject.dto.message;

import ftn.siit.project.isspoject.entity.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewMessageDTO {
    private String text;
    private LocalDateTime time;

    NewMessageDTO(){}
    NewMessageDTO(Message message) {
        this.text = message.getText();
        this.time = LocalDateTime.now();
    }
}
