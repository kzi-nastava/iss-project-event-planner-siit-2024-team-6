package ftn.siit.project.isspoject.dto.message;

import ftn.siit.project.isspoject.entity.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewMessageDTO {
    private String text;

    NewMessageDTO(){}
    NewMessageDTO(Message message, boolean fromUser) {
        this.text = message.getText();
    }
}
