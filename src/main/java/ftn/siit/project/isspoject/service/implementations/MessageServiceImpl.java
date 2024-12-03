package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.service.interfaces.MessageService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final List<Message> messages = new ArrayList<>();
    private Integer messageIdCounter = 1;

    public MessageServiceImpl() {
        // Hardcoded messages
        messages.add(new Message(messageIdCounter++, "Hello, how are you?", ZonedDateTime.now().minusHours(2), 1, 2));
        messages.add(new Message(messageIdCounter++, "I'm good, thanks! How about you?", ZonedDateTime.now().minusHours(1), 2, 1));
        messages.add(new Message(messageIdCounter++, "Doing great! Thanks for asking.", ZonedDateTime.now().minusMinutes(45), 1, 2));
        messages.add(new Message(messageIdCounter++, "Hey, are we still on for tomorrow?", ZonedDateTime.now().minusMinutes(30), 3, 4));
        messages.add(new Message(messageIdCounter++, "Yes, see you at 10 AM.", ZonedDateTime.now().minusMinutes(20), 4, 3));
    }

    //public Page<Message> findAll(Pageable page) {
//        return messageRepository.findAll(page);
//    }

    @Override
    public Message save(Message message) {
        message.setId(messageIdCounter++);
        message.setTime(ZonedDateTime.now());
        messages.add(message);
        return message;
    }

    @Override
    public void delete(Message message) {
        messages.removeIf(m -> m.getId().equals(message.getId()));
    }

    @Override
    public List<Message> findMessagessBetween(Integer user1, Integer user2) {
        return messages.stream()
                .filter(message -> (message.getSender().equals(user1) && message.getReceiver().equals(user2)) ||
                                   (message.getSender().equals(user2) && message.getReceiver().equals(user1)))
                .collect(Collectors.toList());
    }
}
