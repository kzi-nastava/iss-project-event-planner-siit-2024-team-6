package ftn.siit.project.isspoject.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isFromUser")
    private boolean isFromUser;

    public MessageDTO() {}

    public MessageDTO(Message message, boolean fromUser) {
        this.id = message.getId();
        this.text = message.getText();
        this.isFromUser = fromUser;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("isFromUser")
    public boolean isFromUser() {
        return isFromUser;
    }

    @JsonProperty("isFromUser")
    public void setIsFromUser(boolean fromUser) {
        this.isFromUser = fromUser;
    }
}
