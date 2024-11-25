package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.EventDTO;
import ftn.siit.project.isspoject.dto.MessageDTO;
import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.MessageService;
import ftn.siit.project.isspoject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/messages")
public class MessageContoller {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Message message) {
        messageService.save(message);
        return ResponseEntity.ok("Message sent successfully");
    }

    @GetMapping("chat/{userId1}/{userId2}")
    public ResponseEntity<List<MessageDTO>> getMessagesBetweenUsers(@PathVariable int userId1, @PathVariable int userId2){
        List<Message> messages = messageService.findMessagessBetween(userId1, userId2);
        if(messages.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        List<MessageDTO> dtos = messages.stream()
                .map(MessageDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

}
