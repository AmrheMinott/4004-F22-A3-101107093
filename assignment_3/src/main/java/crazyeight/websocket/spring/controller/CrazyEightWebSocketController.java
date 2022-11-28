package crazyeight.websocket.spring.controller;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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

	private int amountDrawn = 0;

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
	@RequestMapping("/round")
	public int getRound() {
		return gameLogic.getRound();
	}

	@GetMapping
	@RequestMapping("/drawCard")
	public Player drawCard(@RequestBody String userName) {
		amountDrawn++;
		String card = gameLogic.drawCard(connectedPlayers.get(currentPlayer).getName(), userName, amountDrawn);
		LOGGER.info("Draw Card {} {} ", card, amountDrawn);

		connectedPlayers.get(currentPlayer).setDeck(gameLogic.getDeck());
		if (!Objects.isNull(card)) {
			connectedPlayers.get(currentPlayer).getHand().add(card);
		}

		return connectedPlayers.get(currentPlayer);
	}

	@PostMapping
	@RequestMapping("/canPlay")
	public boolean canPlay(@RequestBody String userName) {
		int previousPlayerIndex = currentPlayer;
		if (userName.equals(connectedPlayers.get(currentPlayer).getName())) {
			if (gameLogic.shouldSkipPlayer(connectedPlayers.get(currentPlayer), amountDrawn)) {
				currentPlayer = gameLogic.changeSimpleDirection(currentPlayer, connectedPlayers, direction);
				amountDrawn = 0;
				return false;
			}
		}

		return gameLogic.canPlay(userName, connectedPlayers.get(previousPlayerIndex).getName());
	}

	@PostMapping
	@RequestMapping("/postCard")
	public Player postCard(@RequestBody Player player) throws URISyntaxException {
		this.topCard = player.getCard();

		connectedPlayers.get(currentPlayer).setCard(this.topCard);
		connectedPlayers.get(currentPlayer).setHand(player.getHand());
		connectedPlayers.get(currentPlayer).setDeck(gameLogic.getDeck());

		direction = gameLogic.determineDirection(topCard, direction);
		int previousPlayer = currentPlayer;
		currentPlayer = gameLogic.determineCurrentPlayer(currentPlayer, connectedPlayers, direction, topCard);

		// Add two cards to player hands.
		gameLogic.addTwoCardToPlayer(connectedPlayers.get(currentPlayer), topCard);
		amountDrawn = 0;

		// set the top card for the player
		connectedPlayers.get(currentPlayer).setCard(topCard);
		connectedPlayers.get(currentPlayer).setDeck(gameLogic.getDeck());

		this.simpMessagingTemplate.convertAndSend("/topic/playerWS", connectedPlayers.get(currentPlayer));
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
		player.setRound(gameLogic.getRound());
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

	@MessageMapping("/playerUpdate")
	@SendTo("/topic/playerWS")
	public Player playerUpdate() throws Exception {
		return connectedPlayers.get(currentPlayer);
	}

}
