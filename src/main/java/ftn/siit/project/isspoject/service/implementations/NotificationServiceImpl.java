package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.notification.NotificationDTO;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.NotificationRepository;
import ftn.siit.project.isspoject.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private NotificationRepository notificationRepository;
    @Override
    public void notifyAdmin(String s) {}

    @Override
    public Notification findById(Integer id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found with ID: " + id));
    }

    @Override
    public Page<Notification> findByReceiverId(Integer receiverId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByReceiverId(receiverId, pageable);
        return notifications;
    }

    @Override
    public Notification save(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null while saving.");
        }
        return notificationRepository.save(notification);
    }

    @Override
    public void notifyUsers(List<User> users, String message) {
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("User list cannot be null or empty.");
        }
        for (User user : users) {
            notifyUser(user, message);
        }
    }

    @Override
    public void notifyUser(User user, String message) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        Notification notification = new Notification();
        notification.setReceiver(user);
        notification.setText(message);
        notification.setTimestamp(LocalDateTime.now());
        save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), new NotificationDTO(notification));
    }
}
