package crazyeight.websocket.spring.controller;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import crazyeight.gamelogic.GameLogic;
import crazyeight.websocket.spring.model.Player;

@RestController
public class CrazyEightWebSocketController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrazyEightWebSocketController.class);
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ArrayList<Player> connectedPlayers;

	private final int TOTAL_NUMBER_OF_PLAYERS = 3;

	private GameLogic gameLogic;
	private String topCard = "";

	private int currentPlayer = 0;

	private int round = 1;

	private boolean direction = true;

	public CrazyEightWebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		connectedPlayers = new ArrayList<>();
		gameLogic = new GameLogic();
		this.topCard = gameLogic.takeCard();
	}

	@GetMapping
	@RequestMapping("/drawCard")
	public String drawCard() {
		String card = gameLogic.drawCard(connectedPlayers.get(currentPlayer).getName());
		connectedPlayers.get(currentPlayer).setDeck(gameLogic.getDeck());
		return card;
	}

	@GetMapping
	@RequestMapping("/canPlay")
	public boolean canPlay(@RequestBody String userName) {
		return gameLogic.canPlay(userName, currentPlayer, connectedPlayers);
	}

	@PostMapping
	@RequestMapping("/postCard")
	public Player postCard(@RequestBody Player player) throws URISyntaxException {
		this.topCard = player.getCard();

		connectedPlayers.get(currentPlayer).setCard(this.topCard);
		connectedPlayers.get(currentPlayer).setHand(player.getHand());

		direction = gameLogic.determineDirection(topCard, direction);
		int previousPlayer = currentPlayer;
		if (direction) {
			currentPlayer++;
			if (currentPlayer > connectedPlayers.size()) {
				currentPlayer = 0;
			}
		} else {
			currentPlayer--;
			if (currentPlayer < 0) {
				currentPlayer = connectedPlayers.size() - 1;
			}
		}
		return connectedPlayers.get(previousPlayer);
	}

	@PostMapping
	@RequestMapping("/player")
	public Player getCurrentPlayerByName(@RequestBody String userName) throws URISyntaxException {
		for (Player p : connectedPlayers) {
			if (p.getName().equals(userName)) {
				p.setCard(topCard);
				p.setDeck(gameLogic.getDeck());
				p.setOtherPlayers(gameLogic.getOtherPlayers(userName, connectedPlayers));
				return p;
			}
		}
		return null;
	}

	@PostMapping
	@RequestMapping("/createPlayer")
	public Player createPlayer(@RequestBody String userName) throws URISyntaxException {
		LOGGER.info("Post made an addition " + userName);

		for (Player p : connectedPlayers) {
			if (p.getName().equals(userName)) {
				return null;
			}
		}
		Player player = new Player();
		player.setName(userName);
		player.setRound(round);
		player.setScore(0);
		player.setCard(this.topCard);
		connectedPlayers.add(player);
		List<String> hand = new ArrayList<>(Arrays.asList());

		for (int i = 0; i < 5; i++) {
			hand.add(gameLogic.takeCard());
		}

		player.setHand(new ArrayList<>(hand));
		player.setOtherPlayers(gameLogic.getOtherPlayers(userName, connectedPlayers));

		return player;
	}

}
