package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.notification.NewNotificationDTO;
import ftn.siit.project.isspoject.dto.notification.NotificationDTO;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.service.interfaces.NotificationService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/notifications/")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Used to send WebSocket messages

    @PostMapping
    public ResponseEntity<NotificationDTO> addNotification(@RequestBody NewNotificationDTO dto) {
        dto.setTimestamp(LocalDateTime.now());
        Notification notification = new Notification(dto);
        User receiver = userService.findById(dto.getReceiverId());
        notification.setReceiver(receiver);

        Notification saved = notificationService.save(notification);

        // Send the notification via WebSocket
        messagingTemplate.convertAndSend("/socket-publisher/notifications/" + receiver.getId(), new NotificationDTO(saved));

        return ResponseEntity.status(HttpStatus.CREATED).body(new NotificationDTO(saved));
    }

    @PutMapping("{id}")
    public ResponseEntity<NotificationDTO> updateNotification(@PathVariable Integer id, @RequestBody NewNotificationDTO dto) {
        Notification notification = notificationService.findById(id);
        notification.setText(dto.getText());

        Notification updated = notificationService.save(notification);

        // Send the updated notification via WebSocket
        messagingTemplate.convertAndSend("/socket-publisher/notifications/" + updated.getReceiver().getId(), new NotificationDTO(updated));

        return ResponseEntity.ok(new NotificationDTO(updated));
    }
    @GetMapping("receiver/{receiverId}")
    public ResponseEntity<Page<NotificationDTO>> getByReceiver(
            @PathVariable Integer receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<Notification> notificationsPage = notificationService.findByReceiverId(receiverId, pageable);

        Page<NotificationDTO> dtoPage = notificationsPage.map(NotificationDTO::new);

        return ResponseEntity.ok(dtoPage);
    }

}
