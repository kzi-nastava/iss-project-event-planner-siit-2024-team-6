package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.notification.NewNotificationDTO;
import ftn.siit.project.isspoject.dto.notification.NotificationDTO;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.NotificationService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/notifications/")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<NotificationDTO> addNotification(@RequestBody NewNotificationDTO dto) {
        dto.setTimestamp(LocalDateTime.now());
        Notification notification = new Notification(dto);
        User receiver = userService.findById(dto.getReceiverId());
        notification.setReceiver(receiver);

        Notification saved = notificationService.save(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NotificationDTO(saved));

    }

    @PutMapping("{id}")
    public ResponseEntity<NotificationDTO> updateNotification(@PathVariable Integer id, @RequestBody NewNotificationDTO dto) {
        Notification notification = notificationService.findById(id);
        notification.setText(dto.getText());

        Notification updated = notificationService.save(notification);
        return ResponseEntity.ok(new NotificationDTO(updated));
    }


    @GetMapping("receiver/{receiverId}")
    public ResponseEntity<List<NotificationDTO>> getByReceiver(@PathVariable Integer receiverId) {
        List<Notification> notifications = notificationService.findByReceiverId(receiverId);
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<NotificationDTO> dtos = notifications.stream()
                .map(NotificationDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }


}
