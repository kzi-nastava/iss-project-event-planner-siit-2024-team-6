package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.dto.message.NewMessageDTO;
import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.entity.User;

import java.util.List;

public interface MessageService {
    Message save(Message message);
    void delete(Message message);
    Message save(NewMessageDTO newMessageDTO, User sender, User receiver);
    List<Message> findMessagessBetween(Integer user1, Integer user2);
}
