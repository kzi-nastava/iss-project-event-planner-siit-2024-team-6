package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Chat;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.exceptions.NotFoundException;
import ftn.siit.project.isspoject.repository.ChatRepository;
import ftn.siit.project.isspoject.service.interfaces.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService{

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public List<Chat> findAllChatsWithUser(int userId) {
        return chatRepository.findAllWithParticipant(userId);
    }

    @Override
    public Chat blockChat(int chatId) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isPresent()) {
            Chat c = chat.get();
            c.setBlocked(true);
            return chatRepository.save(c);
        } else {
            throw new NotFoundException("Chat with id " + chatId + " not found");
        }
    }

    @Override
    public Chat unblockChat(int chatId) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isPresent()) {
            Chat c = chat.get();
            c.setBlocked(false);
            return chatRepository.save(c);
        } else {
            throw new NotFoundException("Chat with id " + chatId + " not found");
        }
    }

    @Override
    public Chat createChat(User u1, User u2) {
        Chat c = new Chat(u1, u2, Instant.now(), false);
        return chatRepository.save(c);
    }

    @Override
    public Chat updateActivity(int chatId) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isPresent()) {
            Chat c = chat.get();
            c.setLastUpdated(Instant.now());
            return chatRepository.save(c);
        } else {
            throw new NotFoundException("Chat with id " + chatId + " not found");
        }
    }

    @Override
    public Chat findChatById(int chatId) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isPresent()) {
            return chat.get();
        } else {
            throw new NotFoundException("Chat with id " + chatId + " not found");
        }
    }

    @Override
    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }

}
