package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.message.NewMessageDTO;
import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.entity.User;
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
        return messageRepositoy.save(message);
    }

    @Override
    public void delete(Message message) {
        messageRepositoy.delete(message);
    }

    @Override
    public Message save(NewMessageDTO newMessageDTO, User sender, User receiver) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(newMessageDTO.getText());
        message.setTime(LocalDateTime.now());
        return save(message);
    }

    @Override
    public List<Message> findMessagessBetween(Integer user1, Integer user2) {
        return messageRepositoy.findMessagesBetweenUsers(user1, user2);
    }
}
