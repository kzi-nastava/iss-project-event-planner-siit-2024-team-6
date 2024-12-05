package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.repository.NotificationRepository;
import ftn.siit.project.isspoject.service.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Override
    public void notifyAdmin(String s) {}

    @Override
    public Notification findById(Integer id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));
    }

    @Override
    public List<Notification> findByReceiver(User receiver) {
        return notificationRepository.findByReceiverId(receiver.getId());
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void notifyUsers(List<User> users, String message) {
        for (User user : users) {
            notifyUser(user, message);
        }
    }

    @Override
    public void notifyUser(User user, String message) {
        Notification notification = new Notification();
        notification.setReceiver(user);
        notification.setText(message);
        notification.setTimestamp(LocalDateTime.now());
        save(notification);
    }
}
