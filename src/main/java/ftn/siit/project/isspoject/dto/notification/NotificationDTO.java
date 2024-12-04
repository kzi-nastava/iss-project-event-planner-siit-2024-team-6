package ftn.siit.project.isspoject.dto.notification;

import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Integer id;
    private String text;
    private Integer receiverId;

    public NotificationDTO() {}

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.text = notification.getText();
        this.receiverId = notification.getReceiver().getId();
    }
}
