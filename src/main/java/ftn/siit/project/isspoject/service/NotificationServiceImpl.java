package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    //@Autowired
    //private NotificationRepository notificationRepository;
    @Override
    public void notifyAdmin(String s) {}

    @Override
    public Notification findById(Integer id) {
        return null;
    }

    @Override
    public List<Notification> findByRecipient(User recipient) {
        return List.of();
    }

    @Override
    public void save(Notification notification) {

    }

    @Override
    public void notifyUsers(List<User> users, String message) {
        for (User user : users) {
            Notification notification = new Notification();
            notification.setReceiver(user);
            notification.setText(message);
            notification.setTimestamp(LocalDateTime.now());
            this.save(notification);
        }
    }

    @Override
    public void notifyUser(User user, String message) {

    }
}
