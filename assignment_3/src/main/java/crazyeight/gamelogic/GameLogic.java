package crazyeight.gamelogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import constants.CardFaces;
import crazyeight.websocket.spring.model.Player;

public class GameLogic {
	final int SCORE_ONE = 1;
	final int SCORE_TEN = 10;
	final int SCORE_FIFTY = 50;
	private int amountDrawn = 0;

	private String currentPlayerName = "";

	private ArrayList<String> deck = new ArrayList<>(Arrays.asList("2H", "3H", "4H", "5H", "6H", "7H", "8H", "9H",
			"10H", "JH", "QH", "KH", "AH", "2S", "3S", "4S", "5S", "6S", "7S", "8S", "9S", "10S", "JS", "QS", "KS",
			"AS", "2D", "3D", "4D", "5D", "6D", "7D", "8D", "9D", "10D", "JD", "QD", "KD", "AD", "2C", "3C", "4C", "5C",
			"6C", "7C", "8C", "9C", "10C", "JC", "QC", "KC", "AC"));

	public GameLogic() {
		Collections.shuffle(deck);
	}

	public String drawCard(String playerName) {
		if (!playerName.equals(currentPlayerName)) {
			currentPlayerName = playerName;
			amountDrawn ++;
			return takeCard();
		}
		
		if (playerName.equals(currentPlayerName) && amountDrawn < 3) {
			amountDrawn ++;
			return takeCard();
		}
		
		amountDrawn ++;
		return null;
	}

	public String takeCard() {
		return this.deck.remove(0);
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

	public boolean canPlay(String userName, int currentPlayerIndex, ArrayList<Player> players) {
		return players.get(currentPlayerIndex).getName().equals(userName);
	}

	public ArrayList<Player> updateAllPlayerScores(ArrayList<Player> players) {
		ArrayList<Player> updatedPlayers = new ArrayList<Player>();
		for (Player p : players) {
			p.incrementScore(scoreRound(p.getHand()));
			updatedPlayers.add(p);
		}
		return updatedPlayers;
	}

	public ArrayList<String> getDeck() {
		return deck;
	}

	public void setDeck(ArrayList<String> deck) {
		this.deck = deck;
	}
}
