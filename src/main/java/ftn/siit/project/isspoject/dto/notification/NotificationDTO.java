package ftn.siit.project.isspoject.dto.notification;

import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.NotificationType;
import ftn.siit.project.isspoject.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class NotificationDTO {
    private Integer id;
    private String text;
    private Integer receiverId;
    private LocalDateTime timestamp;
    private NotificationType type;

    public NotificationDTO() {}

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.text = notification.getText();
        this.receiverId = notification.getReceiver().getId();
        this.timestamp = notification.getTimestamp();
        this.type = notification.getType();
    }
}
