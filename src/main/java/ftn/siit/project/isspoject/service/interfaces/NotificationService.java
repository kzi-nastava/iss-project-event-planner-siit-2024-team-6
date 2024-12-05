package ftn.siit.project.isspoject.service.interfaces;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;

import java.util.List;

public interface NotificationService {

    Notification findById(Integer id);

    List<Notification> findByReceiver(User recipient);
    void notifyAdmin(String s);

    Notification save(Notification notification);

    void notifyUsers(List<User> users, String message);
    void notifyUser(User user, String message);
}
