package ftn.siit.project.isspoject.repository;

import ftn.siit.project.isspoject.entity.Event;
import ftn.siit.project.isspoject.entity.Notification;
import ftn.siit.project.isspoject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>{
    List<Notification> findByReceiverId(Integer receiverId);
}
