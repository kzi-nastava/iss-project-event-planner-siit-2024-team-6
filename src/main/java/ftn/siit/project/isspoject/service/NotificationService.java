package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;

import java.util.List;

public interface NotificationService {

    Notification findById(Integer id);

    List<Notification> findByRecipient(User recipient);

    void save(Notification notification);

    void notifyUsers(List<User> users, String message);
}
