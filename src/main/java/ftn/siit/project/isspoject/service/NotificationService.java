package ftn.siit.project.isspoject.service;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;

import java.util.List;

public interface NotificationService {

    Notification findById(Integer id);

    List<Notification> findByRecipient(User recipient);
    void notifyAdmin(String s);

    void save(Notification notification);

    void notifyUsers(List<User> users, String message);
    void notifyUser(User user, String message);
}
