package ftn.siit.project.isspoject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftn.siit.project.isspoject.dto.message.NewMessageDTO;
import ftn.siit.project.isspoject.entity.User;
import ftn.siit.project.isspoject.service.interfaces.UserService;
import ftn.siit.project.isspoject.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
public class SocketController {
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private UserService userService;

	@RequestMapping(value="/sendMessageRest", method = RequestMethod.POST)
	public ResponseEntity<?> sendMessage(HttpServletRequest request, @RequestBody NewMessageDTO dto) {
		if (dto.getText() == null || dto.getText().trim().equals("")) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		String jwtToken = this.tokenUtils.getToken(request);
        if (jwtToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = this.tokenUtils.getUsernameFromToken(jwtToken);
        User user = userService.findByEmail(email);
		if (dto.getRecieverId() == null || dto.getRecieverId().trim().equals("")) {
			this.simpMessagingTemplate.convertAndSend("/socket-publisher", dto);
		} else {
			this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + dto.getRecieverId(), dto);
			this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + user.getId().toString(), dto);
		}

		return ResponseEntity.ok(dto);
	}

	@MessageMapping("/send/message")
	public Map<String, String> broadcastNotification(String message) {
		Map<String, String> messageConverted = parseMessage(message);

		if (messageConverted != null) {
			if (messageConverted.containsKey("toId") && messageConverted.get("toId") != null
					&& !messageConverted.get("toId").equals("")) {
				this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + messageConverted.get("toId"),
						messageConverted);
				this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + messageConverted.get("fromId"),
						messageConverted);
			} else {
				this.simpMessagingTemplate.convertAndSend("/socket-publisher", messageConverted);
			}
		}

		return messageConverted;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> parseMessage(String message) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> retVal;

		try {
			retVal = mapper.readValue(message, Map.class); // parsiranje JSON stringa
		} catch (IOException e) {
			retVal = null;
		}

		return retVal;
	}
}
