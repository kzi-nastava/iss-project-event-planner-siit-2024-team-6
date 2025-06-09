package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Chat;
import ftn.siit.project.isspoject.entity.User;

import java.util.List;

public interface ChatService {
    List<Chat> findAllChatsWithUser(int userId);
    Chat blockChat(int chatId);
    Chat unblockChat(int chatId);
    Chat createChat(User u1, User u2);
    Chat updateActivity(int chatId);
    Chat findChatById(int chatId);
    Chat saveChat(Chat chat);
    Chat findChatByUsers(User u1, User u2);
}
