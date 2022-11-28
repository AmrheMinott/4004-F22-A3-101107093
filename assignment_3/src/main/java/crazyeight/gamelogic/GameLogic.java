package crazyeight.gamelogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import constants.CardFaces;
import crazyeight.websocket.spring.model.Player;

public class GameLogic {
	final int SCORE_ONE = 1;
	final int SCORE_TEN = 10;
	final int SCORE_FIFTY = 50;
	private final int MAX_DRAWS = 3;

	private final ArrayList<String> DECK_CARD = new ArrayList<>(Arrays.asList("2H", "3H", "4H", "5H", "6H", "7H", "8H",
			"9H", "10H", "JH", "QH", "KH", "AH", "2S", "3S", "4S", "5S", "6S", "7S", "8S", "9S", "10S", "JS", "QS",
			"KS", "AS", "2D", "3D", "4D", "5D", "6D", "7D", "8D", "9D", "10D", "JD", "QD", "KD", "AD", "2C", "3C", "4C",
			"5C", "6C", "7C", "8C", "9C", "10C", "JC", "QC", "KC", "AC"));

	private ArrayList<String> deck = DECK_CARD;
	private int round = 1;
	private static final Logger LOGGER = LoggerFactory.getLogger(GameLogic.class);

	public GameLogic() {
		Collections.shuffle(deck);
	}

	public String drawCard(String currentPlayerName, String userName) {

		if (!canPlay(userName, currentPlayerName)) {
			return null;
		}

		if (userName.equals(currentPlayerName)) {
			return takeCard();
		}

		return null;
	}

	public String drawCard(String currentPlayerName, String userName, int amountDrawn) {

		if (!canPlay(userName, currentPlayerName)) {
			return null;
		}

		if (userName.equals(currentPlayerName) && amountDrawn <= MAX_DRAWS) {
			return takeCard();
		}

		return null;
	}

	public String takeCard() {
		if (this.deck.size() == 0)
			return null;
		return this.deck.remove(0);
	}

	public void addTwoCardToPlayer(Player player, String topCard) {

		if (topCard.contains(CardFaces.TWO)) {
			String card = "";
			for (int i = 0; i < 2; i++) {
				card = takeCard();
				if (Objects.nonNull(card)) {
					player.getHand().add(card);
				}
			}
		}
	}

	public int determineCurrentPlayer(int currentPlayerIndex, ArrayList<Player> players, boolean direction,
			String topCard) {
		if (topCard.contains(CardFaces.QUEEN)) {
			if (direction) {
				currentPlayerIndex++;
				if (currentPlayerIndex >= players.size()) {
					currentPlayerIndex = 0;
				}
				currentPlayerIndex++;
				if (currentPlayerIndex >= players.size()) {
					currentPlayerIndex = 0;
				}
			} else {
				currentPlayerIndex--;
				if (currentPlayerIndex < 0) {
					currentPlayerIndex = players.size() - 1;
				}
				currentPlayerIndex--;
				if (currentPlayerIndex < 0) {
					currentPlayerIndex = players.size() - 1;
				}
			}
			return currentPlayerIndex;
		}

		if (direction) {
			currentPlayerIndex++;
			if (currentPlayerIndex >= players.size()) {
				currentPlayerIndex = 0;
			}

		} else {
			currentPlayerIndex--;
			if (currentPlayerIndex < 0) {
				currentPlayerIndex = players.size() - 1;
			}
		}

		return currentPlayerIndex;
	}

	public int changeSimpleDirection(int currentPlayerIndex, ArrayList<Player> players, boolean direction) {
		if (direction) {
			currentPlayerIndex++;
			if (currentPlayerIndex >= players.size()) {
				currentPlayerIndex = 0;
			}

		} else {
			currentPlayerIndex--;
			if (currentPlayerIndex < 0) {
				currentPlayerIndex = players.size() - 1;
			}
		}

		return currentPlayerIndex;
	}

	public int scoreRound(ArrayList<String> playerHand) {
		int finalScore = 0;
		if (playerHand.size() == 0) {
			return finalScore;
		}

		for (String card : playerHand) {
			if (card.contains(CardFaces.KING) || card.contains(CardFaces.QUEEN) || card.contains(CardFaces.JACK)) {
				finalScore += SCORE_TEN;
			} else if (card.contains(CardFaces.EIGHT)) {
				finalScore += SCORE_FIFTY;
			} else if (card.contains(CardFaces.ACE)) {
				finalScore += SCORE_ONE;
			} else {
				finalScore += Integer.parseInt(Character.toString(card.charAt(0)));
			}
		}

		return finalScore;
	}

	public boolean determineDirection(String topCard, boolean direction) {
		if (topCard.contains(CardFaces.ACE)) {
			return !direction;
		}
		return direction;
	}

	public ArrayList<Player> getOtherPlayers(String currentPlayerName, ArrayList<Player> players) {
		ArrayList<Player> otherPlayers = new ArrayList<>();
		for (Player p : players) {
			if (p.getName().equals(currentPlayerName)) {
				continue;
			} else {
				otherPlayers.add(p);
			}
		}
		return otherPlayers;
	}

	public boolean canPlay(String userName, String currentPlayer) {
		return currentPlayer.equals(userName);
	}

	public ArrayList<Player> updateAllPlayerScores(ArrayList<Player> players) {
		ArrayList<Player> updatedPlayers = new ArrayList<Player>();
		for (Player p : players) {
			p.incrementScore(scoreRound(p.getHand()));
			updatedPlayers.add(p);
		}
		return updatedPlayers;
	}

	private void resetDeckAndPlayers(ArrayList<Player> players) {
		setDeck(DECK_CARD);
		Collections.shuffle(deck);
		for (Player p : players) {
			p.getHand().clear();
			for (int i = 0; i < 5; i++) {
				p.getHand().add(takeCard());
			}
			p.setRound(round);
			p.setOtherPlayers(getOtherPlayers(p.getName(), players));
		}
	}

	public void handleRoundCompletion(ArrayList<Player> players, String topCard) {

		for (Player p : players) {
			if (p.getHand().size() == 0) {
				round += 1;
				resetDeckAndPlayers(players);
				return;
			}
		}
	}

	public ArrayList<String> getDeck() {
		return deck;
	}

	public void setDeck(ArrayList<String> deck) {
		this.deck = deck;
	}

	public boolean shouldSkipPlayer(Player player, int amountDrawn) {

		boolean hasPlayableCard = false;
		for (String s : player.getHand()) {
			if (s.contains(CardFaces.EIGHT)) {
				hasPlayableCard = true;
				break;
			}
			if (s.contains(Character.toString((player.getCard().charAt(player.getCard().length() - 1))))) {
				hasPlayableCard = true;
				break;
			}
		}
		if (!hasPlayableCard && this.deck.size() == 0) {
			return true;
		}
		if (!hasPlayableCard && amountDrawn >= MAX_DRAWS && this.deck.size() > 0) {
			return true;
		}
		if (hasPlayableCard) {
			return false;
		}
		return false;
	}

	public int getRound() {
		return round;
	}
}
