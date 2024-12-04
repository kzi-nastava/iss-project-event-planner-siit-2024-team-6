package ftn.siit.project.isspoject.dto.notification;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class NewNotificationDTO {
    private String text;
    private Integer receiverId;
    private LocalDateTime timestamp;
}
