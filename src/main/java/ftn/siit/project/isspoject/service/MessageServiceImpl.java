package ftn.siit.project.isspoject.service;

import ftn.siit.project.isspoject.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MessageServiceImpl implements MessageService{
    @Override
    public Message save(Message message) {
        return null;
    }

    @Override
    public void delete(Message message) {

    }

    @Override
    public List<Message> findMessagessBetween(Integer user1, Integer user2) {
        return null;
    }

}
