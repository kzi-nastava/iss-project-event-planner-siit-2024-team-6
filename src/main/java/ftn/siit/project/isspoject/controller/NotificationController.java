package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.NotificationDTO;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/notifications/")

public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("by-recipient/{recipientId}")
    public ResponseEntity<List<NotificationDTO>> getByRecipient(@PathVariable Integer recipientId) {
        User recipient = new User();

        List<Notification> notifications = notificationService.findByRecipient(recipient);

        List<NotificationDTO> dtos = notifications.stream()
                .map(NotificationDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }


}
