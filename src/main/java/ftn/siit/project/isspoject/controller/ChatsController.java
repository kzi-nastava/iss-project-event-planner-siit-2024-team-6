package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.chat.ChatDTO;
import ftn.siit.project.isspoject.dto.message.MessageDTO;
import ftn.siit.project.isspoject.dto.message.NewMessageDTO;
import ftn.siit.project.isspoject.entity.Chat;
import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.interfaces.ChatService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/chats/")
public class ChatsController {
    @Autowired
    private UserService userService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("{chatId}/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody NewMessageDTO dto, @PathVariable Integer chatId, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Chat chat = chatService.findChatById(chatId);
        User user2 = chat.getParticipant1();
        if (chat.getParticipant1().getId() == user.getId()) {
            user2 = chat.getParticipant2();
        }
        Message created = new Message(dto, user, user2);
        chat.getMessages().add(created);
        Chat c = chatService.saveChat(chat);
        messagingTemplate.convertAndSend("/socket-publisher/messages/" + user2.getId(), new MessageDTO(created));

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO(created));
    }

    @GetMapping("{chatId}")
    public ResponseEntity<List<MessageDTO>> getChat(@PathVariable int chatId) {
        Chat chat = chatService.findChatById(chatId);
        List<Message> messages = chat.getMessages();

        List<MessageDTO> dtos = messages.stream()
                .map(MessageDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("chats")
    public ResponseEntity<List<ChatDTO>> getAllChats(HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User u = userService.findByEmail(email);
        if (u == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Chat> chats = chatService.findAllChatsWithUser(u.getId());

        List<ChatDTO> dtos = new ArrayList<>();
        for (Chat chat : chats) {
            if (chat.getParticipant1().getId() == u.getId()) {
                dtos.add(new ChatDTO(chat.getId(), chat.getParticipant2().getName(), chat.getParticipant2().getLastname(), chat.getParticipant2().getPhotoUrl()));
            } else {
                dtos.add(new ChatDTO(chat.getId(), chat.getParticipant1().getName(), chat.getParticipant1().getLastname(), chat.getParticipant1().getPhotoUrl()));
            }
        }
        return ResponseEntity.ok(dtos);
    }
}
