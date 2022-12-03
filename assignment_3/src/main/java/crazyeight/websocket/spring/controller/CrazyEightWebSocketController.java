package crazyeight.websocket.spring.controller;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import constants.CardFaces;
import constants.GameLogicConstants;
import crazyeight.gamelogic.GameLogic;
import crazyeight.websocket.spring.model.Player;

@RestController
public class CrazyEightWebSocketController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrazyEightWebSocketController.class);
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ArrayList<Player> connectedPlayers;

	private final int TOTAL_NUMBER_OF_PLAYERS = 1;

	private GameLogic gameLogic;
	private String topCard = "";

	private int amountDrawn = 0;

	private int currentPlayer = 0;

	private boolean direction = true;

	public CrazyEightWebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.connectedPlayers = new ArrayList<>();
		this.gameLogic = new GameLogic();
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
		userName = userName.replace("\"", "");
		amountDrawn++;

		String card = gameLogic.drawCard(connectedPlayers.get(currentPlayer).getName(), userName, amountDrawn);
		LOGGER.info("Drawn Card {}.", card);

		if (!Objects.isNull(card)) {
			connectedPlayers.get(currentPlayer).getHand().add(card);
		}

		updateBasicPlayerInformation();
		this.simpMessagingTemplate.convertAndSend("/topic/playerWS", connectedPlayers);
		return connectedPlayers.get(currentPlayer);
	}

	@PostMapping
	@RequestMapping("/canPlay")
	public boolean canPlay(@RequestBody String userName) {
		int previousPlayerIndex = currentPlayer;
		userName = userName.replace("\"", "");

		LOGGER.info("Can {} {} play?", connectedPlayers.get(currentPlayer).getName(), userName);

		this.simpMessagingTemplate.convertAndSend("/topic/currentPlayerName",
				connectedPlayers.get(currentPlayer).getName());

		if (connectedPlayers.size() < TOTAL_NUMBER_OF_PLAYERS) {
			return false;
		}

		if (gameLogic.handleRoundCompletion(connectedPlayers, topCard)) {
			LOGGER.info("Round Complete!");
			amountDrawn = 0;
			this.simpMessagingTemplate.convertAndSend("/topic/playerWS", connectedPlayers);
			determineAndSendWinner();
			return false;
		}

		if (userName.equals(connectedPlayers.get(currentPlayer).getName())) {
			if (gameLogic.shouldSkipPlayer(connectedPlayers.get(currentPlayer), amountDrawn)) {
				LOGGER.info("Skipping player {}.", connectedPlayers.get(currentPlayer).getName());
				currentPlayer = gameLogic.changeSimpleDirection(currentPlayer, connectedPlayers, direction);
				amountDrawn = 0;
				return false;
			}
		}

		if (userName.equals(connectedPlayers.get(currentPlayer).getName())) {
			String topCardSuit = Character.toString(topCard.charAt(topCard.length() - 1));
			boolean canPlay = false;
			for (String card : connectedPlayers.get(currentPlayer).getHand()) {
				String playerHandCardNumber = Character.toString(card.charAt(0));
				String playerHandCardSuit = Character.toString(card.charAt(card.length() - 1));
				if (topCardSuit.equals(playerHandCardSuit) || (playerHandCardNumber.equals(CardFaces.EIGHT))) {
					canPlay = true;
				}
			}

			if (canPlay) {
				return canPlay;
			} else {
				if (amountDrawn > GameLogicConstants.MAX_DRAWS) {
					currentPlayer = gameLogic.changeSimpleDirection(currentPlayer, connectedPlayers, direction);
					amountDrawn = 0;
					return canPlay;
				}
			}
		}

		return gameLogic.canPlay(userName, connectedPlayers.get(previousPlayerIndex).getName());
	}

	@PostMapping
	@RequestMapping("/postCard")
	public Player postCard(@RequestBody Player player) throws URISyntaxException {
		this.topCard = player.getCard();
		LOGGER.info("Card {} has been placed by {} ", topCard, player.getName());
		int priorCurrentPlayer = currentPlayer;
		amountDrawn = 0;

		if (gameLogic.handleRoundCompletion(connectedPlayers, topCard)) {
			LOGGER.info("Round Complete !!");
			this.simpMessagingTemplate.convertAndSend("/topic/playerWS", connectedPlayers);
			determineAndSendWinner();
			return connectedPlayers.get(currentPlayer);
		}

		connectedPlayers.get(currentPlayer).setHand(player.getHand());

		direction = gameLogic.determineDirection(topCard, direction);
		int previousPlayer = currentPlayer;

		currentPlayer = gameLogic.determineCurrentPlayer(currentPlayer, connectedPlayers, direction, topCard);

		LOGGER.info("Current player {} is now {}", currentPlayer, connectedPlayers.get(currentPlayer).getName());
		// Add two cards to player hands.
		if (connectedPlayers.get(currentPlayer).getHand().size() == 1) {
			gameLogic.addTwoCardToPlayer(connectedPlayers.get(currentPlayer), topCard);
		}

		updateBasicPlayerInformation();

		this.simpMessagingTemplate.convertAndSend("/topic/direction", direction);
		this.simpMessagingTemplate.convertAndSend("/topic/currentPlayerName",
				connectedPlayers.get(currentPlayer).getName());
		this.simpMessagingTemplate.convertAndSend("/topic/playerWS", connectedPlayers);

		if (topCard.contains(CardFaces.QUEEN)) {
			String urlPath = String.format("/user/%s/queen", connectedPlayers
					.get(gameLogic.changeSimpleDirection(priorCurrentPlayer, connectedPlayers, direction)).getName());

			simpMessagingTemplate.convertAndSend(urlPath, "You lost your turn due to a queen.");
		}

		return connectedPlayers.get(previousPlayer);
	}

	@PostMapping
	@RequestMapping("/player")
	public Player getCurrentPlayerByName(@RequestBody String userName) throws URISyntaxException {
		userName = userName.replace("\"", "");
		for (Player p : connectedPlayers) {
			if (p.getName().equals(userName)) {
				p.setCard(topCard);
				p.setDeck(gameLogic.getDeck());
				p.setOtherPlayersScore(gameLogic.getOtherPlayers(connectedPlayers));
				return p;
			}
		}
		return null;
	}

	@PostMapping
	@RequestMapping("/createPlayer")
	public Player createPlayer(@RequestBody String userName) throws URISyntaxException {
		userName = userName.replace("\"", "");
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
		player.setOtherPlayersScore(gameLogic.getOtherPlayers(connectedPlayers));

		this.simpMessagingTemplate.convertAndSend("/topic/direction", direction);
		this.simpMessagingTemplate.convertAndSend("/topic/currentPlayerName",
				connectedPlayers.get(currentPlayer).getName());

		this.simpMessagingTemplate.convertAndSend("/topic/playerWS", connectedPlayers);
		return player;
	}

	@PostMapping
	@RequestMapping("/testSetPlayers")
	public void setPlayers(@RequestBody ArrayList<Player> players) {
		connectedPlayers.clear();
		for (Player p : players) {
			connectedPlayers.add(p);
		}

		this.simpMessagingTemplate.convertAndSend("/topic/direction", direction);
		this.simpMessagingTemplate.convertAndSend("/topic/currentPlayerName",
				connectedPlayers.get(currentPlayer).getName());

		this.simpMessagingTemplate.convertAndSend("/topic/playerWS", connectedPlayers);
	}

	@GetMapping
	@RequestMapping("/reset")
	public void resetBackEnd() {
		currentPlayer = 0;
		amountDrawn = 0;
		direction = true;
		connectedPlayers.clear();
		gameLogic.resetDeck();
	}

	@PostMapping
	@RequestMapping("/editCurrentPlayer")
	public void editCurrentPlayerbackEnd(@RequestBody int playerIndex) {
		LOGGER.info("playerIndex -> {}", playerIndex);
		currentPlayer = playerIndex;
		this.simpMessagingTemplate.convertAndSend("/topic/currentPlayerName",
				connectedPlayers.get(currentPlayer).getName());
	}
	
	@PostMapping
	@RequestMapping("/drawCardTest")
	public void drawCardTest(@RequestBody ArrayList<Player> players) {
		connectedPlayers.clear();
		for (Player p : players) {
			connectedPlayers.add(p);
		}

		this.simpMessagingTemplate.convertAndSend("/topic/currentPlayerName",
				connectedPlayers.get(currentPlayer).getName());

		this.simpMessagingTemplate.convertAndSend("/topic/playerWS", connectedPlayers);
	}

	private void updateBasicPlayerInformation() {
		for (Player p : connectedPlayers) {
			p.setCard(topCard);
			p.setDeck(gameLogic.getDeck());
			p.setOtherPlayersScore(gameLogic.getOtherPlayers(connectedPlayers));
		}
	}

	private void determineAndSendWinner() {
		String winner = gameLogic.determineWinner(connectedPlayers);
		if (Objects.nonNull(winner)) {
			this.simpMessagingTemplate.convertAndSend("/topic/winner", winner);
			System.exit(1);
		}
	}
}
