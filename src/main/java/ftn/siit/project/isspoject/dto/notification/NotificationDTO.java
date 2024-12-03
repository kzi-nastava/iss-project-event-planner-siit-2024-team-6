package ftn.siit.project.isspoject.dto.notification;

import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;

import java.time.LocalDateTime;

public class NotificationDTO {
    private User receiver;
    private LocalDateTime timestamp;

    public NotificationDTO() {}

    public NotificationDTO(Notification notification) {
        this.timestamp = notification.getTimestamp();
        this.receiver = notification.getReceiver();
    }
}
