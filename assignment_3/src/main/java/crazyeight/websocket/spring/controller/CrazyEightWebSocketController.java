package crazyeight.websocket.spring.controller;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import crazyeight.websocket.spring.model.CrazyEightMessage;

@Controller
public class CrazyEightWebSocketController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrazyEightWebSocketController.class);
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final Set<String> connectedUsers;

	public CrazyEightWebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		connectedUsers = new HashSet<>();
	}

	@MessageMapping("/register")
	@SendToUser("/queue/newMember")
	public Set<String> registerUser(String webChatUsername) {
		if (!connectedUsers.contains(webChatUsername)) {
			LOGGER.info("Person has been added " + webChatUsername);
			connectedUsers.add(webChatUsername);
			simpMessagingTemplate.convertAndSend("/topic/newMember", webChatUsername);
			return connectedUsers;
		} else {
			return new HashSet<>();
		}
	}

	@MessageMapping("/unregister")
	@SendTo("/topic/disconnectedUser")
	public String unregisterUser(String webChatUsername) {
		connectedUsers.remove(webChatUsername);
		LOGGER.info("Person has been REMOVED HIOE " + webChatUsername);
		return webChatUsername;
	}

	@MessageMapping("/message")
	public void greeting(@Payload CrazyEightMessage message) {
		LOGGER.info("Person has been messaged " + message);

		simpMessagingTemplate.convertAndSendToUser(message.getToWhom(), "/msg", message);
	}
}
