package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.ChatWithMessagesDTO;
import ftn.siit.project.isspoject.dto.chat.ChatDTO;
import ftn.siit.project.isspoject.dto.message.MessageDTO;
import ftn.siit.project.isspoject.dto.message.NewMessageDTO;
import ftn.siit.project.isspoject.entity.Chat;
import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.interfaces.ChatService;
import ftn.siit.project.isspoject.service.interfaces.NotificationService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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
    @Autowired
    private NotificationService notificationService;

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
        chatService.updateActivity(c.getId());
        messagingTemplate.convertAndSend("/socket-publisher/messages/" + user2.getId(), new MessageDTO(created, false));
        notificationService.notifyUser(user2, "You have a new message from "+user.getName()+" "+user.getLastname());
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO(created, true));
    }

    @PostMapping("find/{userId}")
    public ResponseEntity<Integer> findChat(@PathVariable Integer userId, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user2 = userService.findById(userId);
        if (user2 == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Chat chat = chatService.findChatByUsers(user, user2);
        if (chat == null) {
            chat = new Chat();
            chat.setParticipant1(user);
            chat.setParticipant2(user2);
            chat.setLastUpdated(Instant.now());
            chat = chatService.saveChat(chat);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(chat.getId());
    }

   @GetMapping(value = "{chatId}")
    public ResponseEntity<ChatWithMessagesDTO> getChat(@PathVariable int chatId, HttpServletRequest request) {
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
        Boolean isCurrentUserFirstParticipant = chat.getParticipant1().getId() == user.getId();
        ChatDTO chatDTO = new ChatDTO(
                chat.getId(),
                isCurrentUserFirstParticipant ? chat.getParticipant2().getName() : chat.getParticipant1().getName(),
                isCurrentUserFirstParticipant ? chat.getParticipant2().getLastname() : chat.getParticipant1().getLastname(),
                isCurrentUserFirstParticipant ? chat.getParticipant2().getPhotoUrl() : chat.getParticipant1().getPhotoUrl()
        );
        List<Message> messages = chat.getMessages();
        List<MessageDTO> dtos = new ArrayList<>();
        for (Message message : messages) {
            if (message.getSender().getId() == user.getId()) {
                dtos.add(new MessageDTO(message, true));
            } else {
                dtos.add(new MessageDTO(message, false));
            }
        }
        ChatWithMessagesDTO response = new ChatWithMessagesDTO(chatDTO, dtos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
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

    @GetMapping("{chatId}/participant")
    public ResponseEntity<?> getOtherParticipant(@PathVariable Integer chatId, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User currentUser = userService.findByEmail(email);
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Chat chat = chatService.findChatById(chatId);
        if (chat == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User otherParticipant;
        if (chat.getParticipant1().getId().equals(currentUser.getId())) {
            otherParticipant = chat.getParticipant2();
        } else if (chat.getParticipant2().getId().equals(currentUser.getId())) {
            otherParticipant = chat.getParticipant1();
        } else {
            return new ResponseEntity<>("User is not part of this chat", HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(otherParticipant.getId());
    }

    @PostMapping("{chatId}/block")
    public ResponseEntity<?> blockChat(@PathVariable Integer chatId, HttpServletRequest request) {
        String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User currentUser = userService.findByEmail(email);
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Chat chat = chatService.findChatById(chatId);
        if (chat == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        boolean isParticipant = chat.getParticipant1().getId().equals(currentUser.getId()) ||
                chat.getParticipant2().getId().equals(currentUser.getId());
        if (!isParticipant) {
            return new ResponseEntity<>("User is not part of this chat", HttpStatus.FORBIDDEN);
        }

        // Set isBlocked to true
        chat.setBlocked(true);
        chatService.saveChat(chat);

        return ResponseEntity.ok().build();
    }


}
