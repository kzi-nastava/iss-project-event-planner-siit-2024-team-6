package ftn.siit.project.isspoject.service.interfaces;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    Notification findById(Integer id);
    Page<Notification> findByReceiverId(Integer receiverId, Pageable pageable);
    void notifyAdmin(String s);

    Notification save(Notification notification);

    void notifyUsers(List<User> users, String message);
    void notifyUser(User user, String message);
}
