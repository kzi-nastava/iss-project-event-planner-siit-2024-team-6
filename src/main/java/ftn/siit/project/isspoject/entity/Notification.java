package ftn.siit.project.isspoject.entity;

import ftn.siit.project.isspoject.dto.notification.NewNotificationDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Notification() {}
    public Notification(NewNotificationDTO dto){
        this.text = dto.getText();
        this.timestamp = dto.getTimestamp();
    }
}
