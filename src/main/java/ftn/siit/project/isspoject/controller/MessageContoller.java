package ftn.siit.project.isspoject.controller;

import ftn.siit.project.isspoject.dto.message.MessageDTO;
import ftn.siit.project.isspoject.entity.Message;
import ftn.siit.project.isspoject.service.interfaces.MessageService;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/messages/")
public class MessageContoller {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @PostMapping("{senderId}/send/{recieverId}")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDTO messageDTO, @PathVariable Integer senderId, @PathVariable Integer recieverId) {
//        messageService.save(new Message(messageDTO, senderId, recieverId));
        return ResponseEntity.ok("Message sent successfully");
    }

    @GetMapping("{userId1}/chat/{userId2}")
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
    //    @GetMapping(value = "/all_elements")
//    public ResponseEntity<PagedResponse<MessageDTO>> getMessagePageAllElements(Pageable page) {
//
//        Page<Message> messagesPage = messageService.findAll(page);
//
//        List<MessageDTO> messageDTOs = messagesPage.stream()
//                .map(MessageDTO::new)
//                .toList();
//
//        PagedResponse<MessageDTO> response = new PagedResponse<>(
//                messageDTOs,
//                messagesPage.getTotalPages(),
//                messagesPage.getTotalElements()
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
