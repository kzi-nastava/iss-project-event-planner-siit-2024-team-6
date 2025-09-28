package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.message.NewMessageDTO;
import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.MessageRepositoy;
import ftn.siit.project.isspoject.service.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    private MessageRepositoy messageRepositoy;
    //public Page<Message> findAll(Pageable page) {
//        return messageRepository.findAll(page);
//    }

    @Override
    public Message save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        return messageRepositoy.save(message);
    }

    @Override
    public void delete(Message message) {
        if (message == null || !messageRepositoy.existsById(message.getId())) {
            throw new NotFoundException("Message not found or already deleted with ID: " + (message != null ? message.getId() : "null"));
        }
        messageRepositoy.delete(message);
    }

    @Override
    public Message save(NewMessageDTO newMessageDTO, User sender, User receiver) {
        if (newMessageDTO == null || sender == null || receiver == null) {
            throw new IllegalArgumentException("NewMessageDTO, sender, and receiver cannot be null.");
        }
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(newMessageDTO.getText());
        message.setTime(LocalDateTime.now());
        return save(message);
    }

    @Override
    public List<Message> findMessagessBetween(Integer user1, Integer user2) {
        List<Message> messages = messageRepositoy.findMessagesBetweenUsers(user1, user2);
        if (messages.isEmpty()) {
            throw new NotFoundException("No messages found between users with IDs: " + user1 + " and " + user2);
        }
        return messages;
    }
}
