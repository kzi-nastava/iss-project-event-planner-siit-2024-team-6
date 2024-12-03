package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Message;

import java.util.List;

public interface MessageService {
    Message save(Message message);
    void delete(Message message);

    List<Message> findMessagessBetween(Integer user1, Integer user2);
}
