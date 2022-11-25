package crazyeight.gamelogic;

import java.util.ArrayList;

import constants.CardFaces;
import crazyeight.websocket.spring.model.Player;

public class GameLogic {
	final int SCORE_ONE = 1;
	final int SCORE_TEN = 10;
	final int SCORE_FIFTY = 50;

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
}
