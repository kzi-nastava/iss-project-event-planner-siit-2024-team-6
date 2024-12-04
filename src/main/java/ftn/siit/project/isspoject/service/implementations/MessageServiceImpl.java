package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.dto.message.NewMessageDTO;
import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.service.interfaces.MessageService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    //public Page<Message> findAll(Pageable page) {
//        return messageRepository.findAll(page);
//    }

    @Override
    public Message save(Message message) {
        return null;
    }

    @Override
    public void delete(Message message) {
       return;
    }

    @Override
    public Message save(NewMessageDTO newMessageDTO, int senderId, int recieverId) {
        return null;
    }

    @Override
    public List<Message> findMessagessBetween(Integer user1, Integer user2) {
        return null;
    }
}
